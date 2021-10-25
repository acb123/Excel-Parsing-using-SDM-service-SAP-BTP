package customer.sdmservice.excelutility.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Currency;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import customer.sdmservice.Model.GetFileResponse;
import customer.sdmservice.Service.SdmService;
import customer.sdmservice.excelutility.entity.BrokerDetails;
import customer.sdmservice.excelutility.entity.BrokerHeader;
import customer.sdmservice.excelutility.entity.BrokerStatementOptionsExercise;
import customer.sdmservice.excelutility.service.DataParsingService;
import customer.sdmservice.Util.CBRemoteApiService;
import customer.sdmservice.Util.ClearingBrokerUtil;
import customer.sdmservice.Util.RemoteApiService;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementTrade;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementOptionExercise;

public class DataParsingServiceImpl implements DataParsingService {

    private final int rowInit = 9;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RemoteApiService ras;

    @Autowired
    private CBRemoteApiService cbRas;

    @Autowired
    private ClearingBrokerUtil clearingBrokerUtil;

    @Autowired
    private SdmService sdmService;

    public SdmService getSdmService() {
        return sdmService;
    }

    public void setSdmService(SdmService sdmService) {
        this.sdmService = sdmService;
    }

    @Autowired
    private BrokerDetails BrD;

    public BrokerDetails getBrokerDetails() {
        return BrD;
    }

    public void setBrokerDetails(BrokerDetails BrD) {
        this.BrD = BrD;
    }

/**
 * This method reads the excel file from Repository and processes it for parsing
 * @param xssfWorkbook
 * @param fileResponse
 * @param subDomainId
 * @param contProcess
 * @return int
 * @throws IOException
 */
    public XSSFWorkbook processExcel(XSSFWorkbook xssfWorkbook, GetFileResponse fileResponse, final String subDomainId) throws IOException {
        InputStream documentFis = fileResponse.getStream();
        logger.info("document file input stream" + documentFis);
        logger.info("document file input stream class" + documentFis.getClass());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(documentFis, baos);
        InputStream processExcel = new ByteArrayInputStream(baos.toByteArray());

        try {
            xssfWorkbook = new XSSFWorkbook(processExcel);
        } catch (Exception e) {
            logger.error("Excel Data File cannot be found");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            throw e;
        }

        return xssfWorkbook;
    }


    @Override
    public void parseExcelData(final String subDomainId) throws IOException, ODataServiceErrorException {

        // Read excel from document service repository
        List<GetFileResponse> fileResponses = getSdmService().getFileInputStream(subDomainId);
        int contProcess = 0;

        for (GetFileResponse fileResponse : fileResponses) {

            XSSFWorkbook xssfWorkbook = null;
            
            try{
                xssfWorkbook = processExcel(xssfWorkbook, fileResponse, subDomainId);
            }catch(Exception e){
                contProcess = 1;
            }

            if (1 == contProcess) {
                continue;
            }

            List<BrokerDetails> BrdL = new ArrayList<>();
            BrokerHeader BrH = new BrokerHeader();
            Sheet sheet = xssfWorkbook.getSheetAt(0);

            int lastRowIndex = -1;
            if (sheet.getPhysicalNumberOfRows() > 0) {
                // getLastRowNum() actually returns an index, not a row number
                lastRowIndex = sheet.getLastRowNum();

                // now, start at end of spreadsheet and work our way backwards until we find a
                // row having data
                for (; lastRowIndex >= 0; lastRowIndex--) {
                    Row row = sheet.getRow(lastRowIndex);
                    if (row != null) {
                        break;
                    }
                }
            }
            System.out.println("Number of rows: " + lastRowIndex);

            // Trading Broker Details Parse and Store
            contProcess = parseStatementDetails(fileResponse, subDomainId, contProcess, lastRowIndex, BrdL, sheet);

            if (1 == contProcess) {
                continue;
            }

            // Trading Broker Header Parse and Store
            contProcess = parseStatementHeader(fileResponse, subDomainId, BrH, contProcess, sheet);

            if (1 == contProcess) {
                continue;
            }

            System.out.println("Statement type: " + BrH.getStatementType());
            if (null == BrH.getStatementType()) {
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                logger.info("Incorrect Statement Format, Please upload proper document");
            } else if (BrH.getStatementType().equalsIgnoreCase("Trading")) {
                BrokerStatementHeader bSH = new BrokerStatementHeader();

                uploadTradingBrokerStatement(fileResponse, subDomainId, BrH, bSH, BrdL, contProcess);

            } else {
                com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader bSH = new com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader();

                uploadClearingBrokerStatement(fileResponse, subDomainId, BrH, bSH, BrdL, contProcess);

            }
        }
    }


/**
 * Method to perform the Clearing Statement Upload
 * @param fileResponse
 * @param subDomainId
 * @param BrH
 * @param bSH
 * @param BrdL
 * @param contProcess
 */
    public void uploadClearingBrokerStatement(GetFileResponse fileResponse, final String subDomainId, BrokerHeader BrH,
            com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader bSH, 
            List<BrokerDetails> BrdL, int contProcess)throws IOException {

        try {
            clearingBrokerUtil.populateBrokerHeader(BrH, bSH, fileResponse);
        } catch (Exception e) {
            logger.info("Incorrect Clearing Statement Header format, Please rectify..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            contProcess = 1;
        }

        try {
            clearingBrokerUtil.populateBrokerDetails(bSH, fileResponse, BrdL);
        } catch (Exception e) {
            logger.info("Incorrect Clearing Statement Details format, Please rectify..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            contProcess = 1;
        }

        if (0 == contProcess) {
            /** Remote service Call **/
            try {
                cbRas.insertBrokerStatement(bSH);
                sdmService.moveObject(subDomainId, HttpStatus.ACCEPTED, fileResponse);
            } catch (ODataException e) {
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                logger.info("Exception for Trading Broker Statement upload" + e.getMessage());
            } catch (Exception e) {
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                logger.info("Exception for Clearing Broker Statement upload" + e.getMessage());
            }
        }

    }


/**
 * Method to perform the Trading Statment Upload
 * @param fileResponse
 * @param subDomainId
 * @param BrH
 * @param bSH
 * @param BrdL
 * @param contProcess
 */
    public void uploadTradingBrokerStatement(GetFileResponse fileResponse, final String subDomainId, BrokerHeader BrH,
            BrokerStatementHeader bSH, List<BrokerDetails> BrdL, int contProcess) throws IOException{

        try {
            populateBrokerHeader(BrH, bSH, fileResponse);
        } catch (Exception e) {
            logger.info("Incorrect Trading Statement Header format, Please rectify..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            contProcess = 1;
        }

        try {
            populateBrokerDetails(bSH, fileResponse, BrdL);
        } catch (Exception e) {
            logger.info("Incorrect Trading Statement Details format, Please rectify..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            contProcess = 1;
        }

        if (0 == contProcess) {
            /** Remote service Call **/
            RemoteApiService ras = new RemoteApiService();
            try {
                ras.insertBrokerStatement(bSH);
                sdmService.moveObject(subDomainId, HttpStatus.ACCEPTED, fileResponse);
            } catch (ODataException e) {
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                logger.info("Exception for Trading Broker Statement upload" + e.getMessage());
            } catch (Exception e) {
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                logger.info("Exception for Trading Broker Statement upload" + e.getMessage());
            }
        }

    }


/**
 *  This method parses the Broker Statement Header and stores the data into appropriate object
 * @param fileResponse
 * @param subDomainId
 * @param BrH
 * @param contProcess
 * @param sheet
 * @return int
 */
    public int parseStatementHeader(GetFileResponse fileResponse, final String subDomainId, BrokerHeader BrH,
            int contProcess, Sheet sheet) throws IOException {

        System.out.println("Header Data Filling");
        logger.info("File Name: " + fileResponse.getFileName());
        // Header Data Filling

        Row row0 = sheet.getRow(0);
        if (((String) getValueFromCell(row0.getCell(0))).equalsIgnoreCase("Broker Id")) {

            try {
                for (int rowNumHeader = 0; rowNumHeader < 7; rowNumHeader++) {
                    Row rowHeader = sheet.getRow(rowNumHeader);

                    if (rowHeader != null) {
                        switch (rowNumHeader) {
                        case 0:
                            BrH.setBrokerID((String) getValueFromCell(rowHeader.getCell(1)));
                            break;

                        case 1:
                            BrH.setAccountNumber((String) getValueFromCell(rowHeader.getCell(1)));
                            break;

                        case 2:
                            String statementString = (String) getValueFromCell(rowHeader.getCell(1));
                            if (statementString != null) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                LocalDate statementDate = LocalDate.parse(statementString, dtf);
                                BrH.setStatementDate(statementDate);
                            } else
                                BrH.setStatementDate(null);
                            break;

                        case 3:
                            String activityFromString = (String) getValueFromCell(rowHeader.getCell(1));
                            if (activityFromString != null) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                LocalDate activityFromDate = LocalDate.parse(activityFromString, dtf);
                                BrH.setActivityFromDate(activityFromDate);
                            } else
                                BrH.setActivityFromDate(null);
                            break;

                        case 4:
                            String activityToString = (String) getValueFromCell(rowHeader.getCell(1));
                            if (activityToString != null) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                                LocalDate activityToDate = LocalDate.parse(activityToString, dtf);
                                BrH.setActivityToDate(activityToDate);
                            } else
                                BrH.setActivityToDate(null);
                            break;
                        case 5:
                            String statementUploadedBy = (String) getValueFromCell(rowHeader.getCell(1));
                            BrH.setStatementUploadedBy(statementUploadedBy);
                            break;
                        case 6:
                            String statementType = (String) getValueFromCell(rowHeader.getCell(1));
                            BrH.setStatementType(statementType);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                logger.info("Incorrect Trading Statement Header format , Please rectify..");
                sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
                contProcess = 1;
            }
          }

        return contProcess;
    }


/**
 * This method performs the parsing of the Broker Statement Details i.e. Confirmations/Trade and Options Exercises 
 * and stores in respective DTOs
 * @param fileResponse
 * @param subDomainId
 * @param contProcess
 * @param lastRowIndex
 * @param BrdL
 * @param sheet
 * @return int
 */
    public int parseStatementDetails(GetFileResponse fileResponse, final String subDomainId, int contProcess,
            int lastRowIndex, List<BrokerDetails> BrdL, Sheet sheet) throws IOException{

        // Broker Details Parse and Store
        System.out.println("Broker Details Parse and Store");
        
        if(rowInit > lastRowIndex){
            logger.info("No Records found for Trading Statment Details..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            return 1;
        }

         try {
            for (int rowNum = rowInit; rowNum <= lastRowIndex; rowNum++) {
                BrokerDetails BD = new BrokerDetails();
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    Iterator<Cell> cellItr = row.cellIterator();
                    while (cellItr.hasNext()) {
                        Cell cell = cellItr.next();
                        int index = cell.getColumnIndex();
                        switch (index) {
                        case 0:
                            BD.setProductCode((String) getValueFromCell(cell));
                            break;
                        case 1:
                            BD.setNumberOfContracts(((Double) getValueFromCell(cell)).intValue());
                            break;
                        case 2:
                            if (cell.getCellType() == CellType.BLANK) {
                                BD.setExercisedPosition(
                                        ((Double) NumberUtils.toDouble((String) getValueFromCell(cell))).intValue());
                            } else {
                                BD.setExercisedPosition(((Double) getValueFromCell(cell)).intValue());
                            }
                            break;
                        case 3:
                            String direction = (String) getValueFromCell(cell);
                            if (direction.equalsIgnoreCase("Buy")) {
                                BD.setDirection("L");
                            } else if (direction.equalsIgnoreCase("Sell")) {
                                BD.setDirection("S");
                            } else
                                BD.setCallPut(null);
                            break;
                        case 4:
                            BD.setContractPrice((Double) getValueFromCell(cell));
                            break;
                        case 5:
                            BD.setCurrency((String) getValueFromCell(cell));
                            break;
                        case 6:
                            if (cell.getCellType() == CellType.BLANK) {
                                BD.setStrikePrice(null);
                            } else {
                                BD.setStrikePrice((Double) getValueFromCell(cell));
                            }
                            break;
                        case 7:
                            String CallPut = (String) getValueFromCell(cell);
                            if (CallPut.equalsIgnoreCase("Call")) {
                                BD.setCallPut(2);
                            } else if (CallPut.equalsIgnoreCase("Put")) {
                                BD.setCallPut(1);
                            } else
                                BD.setCallPut(null);
                            break;
                        case 8:
                            BD.setTradeDate((String) getValueFromCell(cell));
                            break;
                        case 9:
                            BD.setExerciseDate((String) getValueFromCell(cell));
                            break;
                        case 10:
                            BD.setProductSymbol((String) getValueFromCell(cell));
                            break;
                        case 11:
                            BD.setMaturityDate((String) getValueFromCell(cell));
                            break;
                        case 12:
                            String DerivativeCategory = (String) getValueFromCell(cell);
                            if (DerivativeCategory.equalsIgnoreCase("Futures")) {
                                BD.setDerivativeCategory(1);
                            } else if (DerivativeCategory.equalsIgnoreCase("Options")) {
                                BD.setDerivativeCategory(2);
                            } else
                                BD.setDerivativeCategory(null);
                            break;
                        case 13:
                            BD.setType((String) getValueFromCell(cell));
                            break;
                        }
                    }
                    BrdL.add(BD);

                }
            }
        } catch (Exception e) {
            logger.info("Incorrect Trading Statement Details format , Please rectify..");
            sdmService.moveObject(subDomainId, HttpStatus.INTERNAL_SERVER_ERROR, fileResponse);
            contProcess = 1;
        }

        return contProcess;

    }


/**
 * This method populates the Trading Broker Header Object
 * @param BrH
 * @param bSH
 * @param fileResponse
 */
    public void populateBrokerHeader(BrokerHeader BrH, BrokerStatementHeader bSH, GetFileResponse fileResponse) throws IOException{
        /** Set Admin fields for header */
        System.out.println("Set Admin fields for header");
        bSH.setCreatedBy(fileResponse.getCreatedBy());
        bSH.setModifiedBy(fileResponse.getChangedBy());
        bSH.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
        bSH.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
        bSH.setBrokerID(BrH.getBrokerID());
        bSH.setAccountNumber(BrH.getAccountNumber());
        bSH.setStatementDate(BrH.getStatementDate().atStartOfDay());
        bSH.setActivityFromDate(BrH.getActivityFromDate().atStartOfDay());
        bSH.setActivityToDate(BrH.getActivityToDate().atStartOfDay());
        bSH.setStatementType(1);
        bSH.setStatementUploadedBy(BrH.getStatementUploadedBy());

    }


/**
 * This method populates the Trading Broker Details Object which incldes Confirmation and Options Exercise
 * @param bSH
 * @param fileResponse
 * @param BrdL
 */
    public void populateBrokerDetails(BrokerStatementHeader bSH, GetFileResponse fileResponse,
            List<BrokerDetails> BrdL) {

        // // Create Confirmation and Options exercise objects for Persistency
        List<BrokerStatementTrade> BrT = new ArrayList<>();
        List<BrokerStatementOptionExercise> BrOE = new ArrayList<>();

        System.out.println("Create Confirmation and Options exercise objects for Persistency");
        for (BrokerDetails bdp : BrdL) {
            /** For Confirmation Statments */
            if (bdp.getType().equalsIgnoreCase("Confirmation")) {
                BrokerStatementTrade bst = new BrokerStatementTrade();
                // CreatedBy
                bst.setCreatedBy(fileResponse.getCreatedBy());
                // ModifiedBy
                bst.setModifiedBy(fileResponse.getChangedBy());
                // CreatedAt
                bst.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
                // ModifiedAt
                bst.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
                // No of Contracts
                bst.setNoOfContracts(bdp.getNumberOfContracts());
                // Contract Price
                bst.setContractPrice(bdp.getContractPrice());
                // Strike Price
                bst.setStrikePrice(bdp.getStrikePrice());
                // Confirmation Date
                System.out.println("Confirmation Date");
                String dateString = bdp.getTradeDate();
                if (dateString != null) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate tradeDate = LocalDate.parse(dateString, dtf);
                    bst.setTradeDate(tradeDate.atStartOfDay());
                } else
                    bst.setTradeDate(null);
                // Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString, dtf);
                bst.setMaturityDate(matDate.atStartOfDay());
                // Product Code
                bst.setProductCodeExchange(bdp.getProductCode());
                // Currency
                // bst.setCurrency(Currency.getInstance(bdp.getCurrency()));
                bst.setCurrency_code(bdp.getCurrency());
                // Product Symbol
                bst.setProductSymbol(bdp.getProductSymbol());
                // PutCall Indicator
                bst.setPutCallIndicator(bdp.getCallPut());
                // Derivative Category
                bst.setDerivativeCategory(bdp.getDerivativeCategory());
                // Direction
                bst.setDirection(bdp.getDirection());
                BrT.add(bst);
            }
            /** For Option Exercises */
            else if (bdp.getType().equalsIgnoreCase("Options Exercise")) {
                BrokerStatementOptionExercise bsoe = new BrokerStatementOptionExercise();
                // CreatedBy
                bsoe.setCreatedBy(fileResponse.getCreatedBy());
                // ModifiedBy
                bsoe.setModifiedBy(fileResponse.getChangedBy());
                // CreatedAt
                bsoe.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
                // ModifiedAt
                bsoe.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
                // Product Code
                bsoe.setProductCodeExchange(bdp.getProductCode());
                // Strike Price
                bsoe.setStrikePrice(bdp.getStrikePrice());
                // PutCall Indicator
                bsoe.setPutCallIndicator(bdp.getCallPut());
                // Total Position
                bsoe.setTotalPosition(bdp.getNumberOfContracts());
                // Remaining Position
                bsoe.setRemPosition(bdp.getRemainingPosition());
                // Exercised Position
                bsoe.setExercisedPosition(bdp.getExercisedPosition());
                // Direction
                bsoe.setDirection(bdp.getDirection());
                // Premium
                bsoe.setContractPrice(bdp.getContractPrice());
                // Settlement Price
                bsoe.setSettlementPrice(bdp.getSettlementPrice());
                // Currency
                // bsoe.setCurrency(Currency.getInstance(bdp.getCurrency()));
                bsoe.setCurrency_code(bdp.getCurrency());
                // Exercise Date
                String dateString = bdp.getExerciseDate();
                if ((!dateString.isEmpty()) || dateString != null) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate exDate = LocalDate.parse(dateString, dtf); // .toLocalDate();
                    // Instant exInstant = exDate.toInstant(ZoneOffset.UTC);
                    bsoe.setExerciseDate(exDate.atStartOfDay());
                } else
                    bsoe.setExerciseDate(null);
                // Product Symbol
                bsoe.setProductSymbol(bdp.getProductSymbol());
                // Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString, dtf);
                bsoe.setMaturityDate(matDate.atStartOfDay());
                // Settlement Type
                bsoe.setSettlementType(bdp.getSettlementType());

                BrOE.add(bsoe);
            }
        }

        /**
         * Assign List of Confirmations and Option Exercises to Broker Statement Header
         */
        bSH.setBrokerStatementTrade(BrT);
        bSH.setBrokerStatementOptionExercise(BrOE);

        // Print Broker Header , Confirmations and Option Exercises

        logger.info(bSH.getAccountNumber() + " " + bSH.getBrokerID() + " " + bSH.getStatementDate() + " "
                + bSH.getActivityFromDate() + " " + bSH.getActivityToDate() + " " + bSH.getCreatedBy() + " "
                + bSH.getModifiedBy() + " " + bSH.getCreatedAt() + " " + bSH.getModifiedAt());

        logger.info("CONFIRMATIONS:");
        for (BrokerStatementTrade bst : BrT) {
            logger.info(bst.getProductCodeExchange() + " " + bst.getNoOfContracts() + " " + bst.getContractPrice() + " "
                    + bst.getStrikePrice() + " " + bst.getDirection() + " " + bst.getCurrency_code() + " "
                    + bst.getPutCallIndicator() + " " + bst.getTradeDate() + " " + bst.getProductSymbol() + " "
                    + bst.getMaturityDate() + " " + bst.getDerivativeCategory());
        }

        logger.info("OPTIONS EXERCISES:");
        for (BrokerStatementOptionExercise boe : BrOE) {
            logger.info(boe.getProductCodeExchange() + " " + boe.getTotalPosition() + " " + boe.getRemPosition() + " "
                    + boe.getExercisedPosition() + " " + boe.getStrikePrice() + " " + boe.getDirection() + " "
                    + boe.getCurrency_code() + " " + boe.getPutCallIndicator() + " " + boe.getExerciseDate() + " "
                    + boe.getProductSymbol() + " " + boe.getMaturityDate() + " " + boe.getContractPrice());
        }
    }


/**
 * Method to get cell value based on cell type
 * @param cell
 * @return Object
 */
    private Object getValueFromCell(Cell cell) {
        switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case BOOLEAN:
            return cell.getBooleanCellValue();
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                // return cell.getDateCellValue();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                return sdf.format(cell.getDateCellValue());
            }
            return cell.getNumericCellValue();
        case FORMULA:
            return cell.getCellFormula();
        case BLANK:
            return "";
        default:
            return "";
        }
    }


}
