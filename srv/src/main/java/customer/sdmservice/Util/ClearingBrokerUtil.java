package customer.sdmservice.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import customer.sdmservice.Model.GetFileResponse;
import customer.sdmservice.excelutility.entity.BrokerDetails;
import customer.sdmservice.excelutility.entity.BrokerHeader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementConfirmation;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementOptionExercise;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementLongShortMatch;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementOpenPosition;

@Component
public class ClearingBrokerUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public void populateBrokerHeader(BrokerHeader BrH, BrokerStatementHeader bSH, GetFileResponse fileResponse){
        /**Set Admin fields for header*/     
        bSH.setCreatedBy(fileResponse.getCreatedBy());
        bSH.setModifiedBy(fileResponse.getChangedBy());
        bSH.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
        bSH.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
        bSH.setBrokerID(BrH.getBrokerID());
        bSH.setAccountNumber(BrH.getAccountNumber());
        bSH.setStatementDate(BrH.getStatementDate().atStartOfDay());
        bSH.setActivityFromDate(BrH.getActivityFromDate().atStartOfDay());
        bSH.setActivityToDate(BrH.getActivityToDate().atStartOfDay());
        bSH.setStatementType(2);
        bSH.setStatementUploadedBy(BrH.getStatementUploadedBy());

    }

    public void populateBrokerDetails(BrokerStatementHeader bSH, GetFileResponse fileResponse, List<BrokerDetails> BrdL){


// // Create Confirmations, Options exercise, Long Short Match and Open Position objects for Persistency
        List<ClearingBrokerStatementConfirmation> BrT = new ArrayList<>();
        List<ClearingBrokerStatementOptionExercise> BrOE = new ArrayList<>();
        List<ClearingBrokerStatementLongShortMatch> BrLSM = new ArrayList<>();
        List<ClearingBrokerStatementOpenPosition> BrOP = new ArrayList<>();
      
        for(BrokerDetails bdp : BrdL){
/** For Confirmations Statments */            
            if(bdp.getType().equalsIgnoreCase("Confirmation")){
                ClearingBrokerStatementConfirmation bst = new ClearingBrokerStatementConfirmation();
            //CreatedBy
                bst.setCreatedBy(fileResponse.getCreatedBy());
            //ModifiedBy    
                bst.setModifiedBy(fileResponse.getChangedBy());
            //CreatedAt    
                bst.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
            //ModifiedAt
                bst.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
            //No of Contracts
                bst.setNoOfContracts(bdp.getNumberOfContracts());
            //Contract Price
                bst.setContractPrice(bdp.getContractPrice());
            //Strike Price
                bst.setStrikePrice(bdp.getStrikePrice());
                //Trade Date
                String dateString = bdp.getTradeDate();
                if(dateString!=null){
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate tradeDate = LocalDate.parse(dateString,dtf);
                bst.setTradeDate(tradeDate.atStartOfDay());
                }
                else 
                bst.setTradeDate(null);
            //Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString,dtf);
                bst.setMaturityDate(matDate.atStartOfDay());
            //Product Code 
                bst.setProductCodeExchange(bdp.getProductCode());
            //Currency
                bst.setCurrency_code(bdp.getCurrency());
            //Product Symbol
                bst.setProductSymbol(bdp.getProductSymbol());
            //PutCall Indicator
                bst.setPutCallIndicator(bdp.getCallPut());
            //Derivative Category
                bst.setDerivativeCategory(bdp.getDerivativeCategory());
            //Direction
                bst.setDirection(bdp.getDirection());
    
                BrT.add(bst);
            }
/**For Option Exercises */
            else if(bdp.getType().equalsIgnoreCase("Options Exercise")){
                ClearingBrokerStatementOptionExercise bsoe = new ClearingBrokerStatementOptionExercise();
            //CreatedBy
                bsoe.setCreatedBy(fileResponse.getCreatedBy());
            //ModifiedBy    
                bsoe.setModifiedBy(fileResponse.getChangedBy());
            //CreatedAt    
                bsoe.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
            //ModifiedAt
                bsoe.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));        
            //Product Code
                bsoe.setProductCodeExchange(bdp.getProductCode());      
            //Strike Price
                bsoe.setStrikePrice(bdp.getStrikePrice());  
            //PutCall Indicator
                bsoe.setPutCallIndicator(bdp.getCallPut());    
            //Total Position
                bsoe.setTotalPosition(bdp.getNumberOfContracts());
            //Remaining Position
                bsoe.setRemPosition(bdp.getRemainingPosition());
            //Exercised Position
                bsoe.setExercisedPosition(bdp.getExercisedPosition());    
            //Direction
                bsoe.setDirection(bdp.getDirection());
            //Premium
                bsoe.setContractPrice(bdp.getContractPrice());
            //Settlement Price
                bsoe.setSettlementPrice(bdp.getSettlementPrice());
            //Currency
                bsoe.setCurrency_code(bdp.getCurrency());                
                //Exercise Date
                String dateString = bdp.getExerciseDate();
                if((!dateString.isEmpty()) || dateString!=null){
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate exDate = LocalDate.parse(dateString, dtf); 
                bsoe.setExerciseDate(exDate.atStartOfDay());
                }
                else
                bsoe.setExerciseDate(null);
            //Product Symbol 
                bsoe.setProductSymbol(bdp.getProductSymbol());
            //Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString,dtf);
                bsoe.setMaturityDate(matDate.atStartOfDay());
            //Settlement Type
                bsoe.setSettlementType(bdp.getSettlementType());    
            
                BrOE.add(bsoe);
            }
/** For LongShortMatch Statments */            
            if(bdp.getType().equalsIgnoreCase("LongShortMatch")){
                ClearingBrokerStatementLongShortMatch bsLSM = new ClearingBrokerStatementLongShortMatch();
            //CreatedBy
                bsLSM.setCreatedBy(fileResponse.getCreatedBy());
            //ModifiedBy    
                bsLSM.setModifiedBy(fileResponse.getChangedBy());
            //CreatedAt    
                bsLSM.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
            //ModifiedAt
                bsLSM.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
            //No of Contracts
                bsLSM.setNoOfContracts(bdp.getNumberOfContracts());
            //Contract Price
                bsLSM.setContractPrice(bdp.getContractPrice());
            //Strike Price
                bsLSM.setStrikePrice(bdp.getStrikePrice());
                //Trade Date
                String dateString = bdp.getTradeDate();
                if(dateString!=null){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate tradeDate = LocalDate.parse(dateString,dtf);
                    bsLSM.setTradeDate(tradeDate.atStartOfDay());
                }
                else 
                    bsLSM.setTradeDate(null);
            //Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString,dtf);
                bsLSM.setMaturityDate(matDate.atStartOfDay());
            //Product Code 
                bsLSM.setProductCodeExchange(bdp.getProductCode());
            //Currency
                bsLSM.setCurrency_code(bdp.getCurrency());
            //Product Symbol
                bsLSM.setProductSymbol(bdp.getProductSymbol());
            //PutCall Indicator
                bsLSM.setPutCallIndicator(bdp.getCallPut());
            //Derivative Category
                bsLSM.setDerivativeCategory(bdp.getDerivativeCategory());
            //Direction
                bsLSM.setDirection(bdp.getDirection());

                BrLSM.add(bsLSM);
            }
/** For OpenPosition Statments */            
            if(bdp.getType().equalsIgnoreCase("OpenPosition")){
                ClearingBrokerStatementOpenPosition bsOP = new ClearingBrokerStatementOpenPosition();
            //CreatedBy
                bsOP.setCreatedBy(fileResponse.getCreatedBy());
            //ModifiedBy    
                bsOP.setModifiedBy(fileResponse.getChangedBy());
            //CreatedAt    
                bsOP.setCreatedAt(fileResponse.getCreatedAt().atZone(ZoneId.systemDefault()));
            //ModifiedAt
                bsOP.setModifiedAt(fileResponse.getChangedAt().atZone(ZoneId.systemDefault()));
            //No of Contracts
                bsOP.setNoOfContracts(bdp.getNumberOfContracts());
            //Contract Price
                bsOP.setContractPrice(bdp.getContractPrice());
            //Strike Price
                bsOP.setStrikePrice(bdp.getStrikePrice());
                //Trade Date
                String dateString = bdp.getTradeDate();
                if(dateString!=null){
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                    LocalDate tradeDate = LocalDate.parse(dateString,dtf);
                    bsOP.setTradeDate(tradeDate.atStartOfDay());
                }
                else 
                    bsOP.setTradeDate(null);
            //Maturity Date
                String MatString = bdp.getMaturityDate();
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                LocalDate matDate = LocalDate.parse(MatString,dtf);
                bsOP.setMaturityDate(matDate.atStartOfDay());
            //Product Code 
                bsOP.setProductCodeExchange(bdp.getProductCode());
            //Currency
                bsOP.setCurrency_code(bdp.getCurrency());
            //Product Symbol
                bsOP.setProductSymbol(bdp.getProductSymbol());
            //PutCall Indicator
                bsOP.setPutCallIndicator(bdp.getCallPut());
            //Derivative Category
                bsOP.setDerivativeCategory(bdp.getDerivativeCategory());
            //Direction
                bsOP.setDirection(bdp.getDirection());

                BrOP.add(bsOP);
            }
        }

/** Assign List of Confirmations, Option Exercises, Long Short Match and Open Position to Broker Statement Header */     
        bSH.setBrokerStatementConfirmation(BrT);
        bSH.setBrokerStatementOptionExercise(BrOE);
        bSH.setBrokerStatementLongShortMatch(BrLSM);
        bSH.setBrokerStatementOpenPosition(BrOP);


//Print Broker Header , Confirmations, Option Exercises, Long Short Match and Open Position
        
        logger.info(bSH.getAccountNumber() + " " + bSH.getBrokerID() + " " + bSH.getStatementDate() + " " + bSH.getActivityFromDate() 
        + " " + bSH.getActivityToDate() + " " + bSH.getCreatedBy() + " " +bSH.getModifiedBy() + " " + bSH.getCreatedAt() + " " + bSH.getModifiedAt());

        logger.info("CONFIRMATIONS:");
        for(ClearingBrokerStatementConfirmation bst : BrT){
            logger.info( bst.getProductCodeExchange()  + " " + bst.getNoOfContracts() + " " + bst.getContractPrice() + " " + bst.getStrikePrice() 
            + " " + bst.getDirection()       + " " + bst.getCurrency_code()      + " "  + bst.getPutCallIndicator()  + " " + bst.getTradeDate() 
            + " " + bst.getProductSymbol()   + " " + bst.getMaturityDate()  + " "  + bst.getDerivativeCategory());
        }

        logger.info("OPTIONS EXERCISES:");
        for(ClearingBrokerStatementOptionExercise boe : BrOE){
            logger.info( boe.getProductCodeExchange()  + " " + boe.getTotalPosition() + " " + boe.getRemPosition() +  " " + boe.getExercisedPosition() 
            +" " + boe.getStrikePrice()   + " " + boe.getDirection()  + " " + boe.getCurrency_code()      + " "  + boe.getPutCallIndicator() 
            + " " + boe.getExerciseDate() + " " + boe.getProductSymbol()   + " " + boe.getMaturityDate()  + " "  + boe.getContractPrice() + " " + boe.getSettlementPrice()
            + " " + boe.getSettlementType());
        }

        logger.info("LONG SHORT MATCH:");
        for(ClearingBrokerStatementLongShortMatch bsLSM : BrLSM){
            logger.info( bsLSM.getProductCodeExchange()  + " " + bsLSM.getNoOfContracts() + " " + bsLSM.getContractPrice() + " " + bsLSM.getStrikePrice() 
            + " " + bsLSM.getDirection()       + " " + bsLSM.getCurrency_code()      + " "  + bsLSM.getPutCallIndicator()  + " " + bsLSM.getTradeDate() 
            + " " + bsLSM.getProductSymbol()   + " " + bsLSM.getMaturityDate()  + " "  + bsLSM.getDerivativeCategory());
        }

        logger.info("OPEN POSITION:");
        for(ClearingBrokerStatementOpenPosition bsOP : BrOP){
            logger.info( bsOP.getProductCodeExchange()  + " " + bsOP.getNoOfContracts() + " " + bsOP.getContractPrice() + " " + bsOP.getStrikePrice() 
            + " " + bsOP.getDirection()       + " " + bsOP.getCurrency_code()      + " "  + bsOP.getPutCallIndicator()  + " " + bsOP.getTradeDate() 
            + " " + bsOP.getProductSymbol()   + " " + bsOP.getMaturityDate()  + " "  + bsOP.getDerivativeCategory());
        }
    }
}
