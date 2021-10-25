package customer.sdmservice.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementTrade;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementOptionExercise;
import com.sdmservice.vdm.services.TradingBrokerStatementService;
import com.sdmservice.vdm.services.DefaultTradingBrokerStatementService;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

       private final Logger logger = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/test")
	String test() {
        System.out.println("^^^^^^^^^^^^");
        System.out.println("^^^^^^^^^^^^");
		Map<String, List<BrokerStatementHeader>> data = new HashMap<>();
        List<BrokerStatementHeader> finalResults = new ArrayList<BrokerStatementHeader>();

        final HttpDestination httpDestination = DestinationAccessor.getDestination("brRecon")
        .asHttp().decorate(DefaultErpHttpDestination::new);

        logger.info("Http Destination Created for {}", "brRecon");
        final HttpClient client = HttpClientAccessor.getHttpClient(httpDestination);
        String query = "";
        TradingBrokerStatementService serv = new DefaultTradingBrokerStatementService();
        BrokerStatementHeader header = new BrokerStatementHeader();
        setData(header);
        logger.info( "Type client Results {}", header);
        try{
        serv.createBrokerStatementHeader(header).execute(httpDestination);
        logger.info( "Request successful");
        }
        catch(ODataServiceErrorException e){
            logger.info("The OData service responded with an error: {}", e.getOdataError());
        }
        catch(ODataException e){
            logger.error("Error response while sending request: {}", e);
        }
        return "Success";
    }

    private void setData(BrokerStatementHeader header){
        header.setBrokerID("JPMRGN");
        //header.setBusinessPartnerID("xyz");
        header.setActivityFromDate(LocalDateTime.now());
        header.setActivityToDate(LocalDateTime.now());
        header.setAccountNumber("Performance5");
        header.setStatementUploadedBy("devanahalli.sunil.abhishek@sap.com");
        header.setStatementDate(LocalDateTime.now());
        List<BrokerStatementTrade> trades = new ArrayList<>();
        header.setBrokerStatementTrade(trades);
        List<BrokerStatementOptionExercise> ops = new ArrayList<>();
        header.setBrokerStatementOptionExercise(ops);
    }
    
}
