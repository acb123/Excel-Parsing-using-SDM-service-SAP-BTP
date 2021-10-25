package customer.sdmservice.handlers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.http.client.HttpClient;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;

import com.sap.cds.services.cds.CdsService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.catalogservice.CatalogService_;
import cds.gen.catalogservice.Books;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementConfirmation;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementOptionExercise;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementLongShortMatch;
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.ClearingBrokerStatementOpenPosition;
import com.sdmservice.vdm.services.UploadClearingTradingBrokerStatementService;
import com.sdmservice.vdm.services.DefaultUploadClearingTradingBrokerStatementService;

@Component
@ServiceName(CatalogService_.CDS_NAME)
public class CatalogServiceHandler implements EventHandler {

    private static final Logger logger = CloudLoggerFactory.getLogger(CatalogServiceHandler.class);

	@Before(event = CdsService.EVENT_READ)
	public void discountBooks(Stream<Books> books) {
        System.out.println("^^^^^^^^^^^^");
        System.out.println("^^^^^^^^^^^^");
		Map<String, List<BrokerStatementHeader>> data = new HashMap<>();
        List<BrokerStatementHeader> finalResults = new ArrayList<BrokerStatementHeader>();

        final HttpDestination httpDestination = DestinationAccessor.getDestination("brReconCB")
        .asHttp().decorate(DefaultErpHttpDestination::new);

        logger.info("Http Destination Created for {} : {}", httpDestination.getUri(), httpDestination.getAuthenticationType());
        final HttpClient client = HttpClientAccessor.getHttpClient(httpDestination);
        String query = "";
        UploadClearingTradingBrokerStatementService serv = new DefaultUploadClearingTradingBrokerStatementService();
        BrokerStatementHeader header = new BrokerStatementHeader();
        setData(header);
        logger.info( "Type client Results {}", header);
        try{
            serv.createBrokerStatementHeader(header).execute(httpDestination);
            logger.info("Request successful");
        }
        catch(ODataServiceErrorException e){
            logger.info("The OData service responded with an error: {}", e.getOdataError());
        }
        catch(ODataException e){
            logger.error("Error response while sending request: {}", e);
        }
    }
    
   /*private void setData(BrokerStatementHeader header){
        header.setBrokerName("DEUBA");
        header.setBusinessPartnerID("xyz");
        header.setActivityDateFrom(LocalDate.now());
        header.setActivityDateTo(LocalDate.now());
        List<BrokerStatementTrade> trades = new ArrayList<>();
        BrokerStatementTrade trade = new BrokerStatementTrade();
        trade.setDerivativeCategory(1);
        trades.add(trade);
        header.setBrokerStatementTrade(trades);
        List<BrokerStatementOptionExercise> ops = new ArrayList<>();
        BrokerStatementOptionExercise op = new BrokerStatementOptionExercise();
        op.setPutCallIndicator(1);
        ops.add(op);
        header.setBrokerStatementOptionExercise(ops);
    }*/

    private void setData(BrokerStatementHeader header){
        header.setBrokerID("BANK_DE");
        //header.setBusinessPartnerID("xyz");
        header.setActivityFromDate(LocalDateTime.now());
        header.setActivityToDate(LocalDateTime.now());
        header.setAccountNumber("AN1234567");
        header.setStatementDate(LocalDateTime.now());
        header.setStatementUploadedBy("devanahalli.sunil.abhishek@sap.com");
        List<ClearingBrokerStatementConfirmation> confirmations = new ArrayList<>();
        header.setBrokerStatementConfirmation(confirmations);
        List<ClearingBrokerStatementOptionExercise> ops = new ArrayList<>();
        header.setBrokerStatementOptionExercise(ops);
        List<ClearingBrokerStatementLongShortMatch> lsm = new ArrayList<>();
        header.setBrokerStatementLongShortMatch(lsm);
        List<ClearingBrokerStatementOpenPosition> op = new ArrayList<>();
        header.setBrokerStatementOpenPosition(op);
    }

}