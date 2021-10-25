package customer.sdmservice.Util;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementTrade;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementOptionExercise;
import com.sdmservice.vdm.services.TradingBrokerStatementService;
import com.sdmservice.vdm.services.DefaultTradingBrokerStatementService;
import org.springframework.stereotype.Component;


@Component
public class RemoteApiService {

    private static final Logger logger = CloudLoggerFactory.getLogger(RemoteApiService.class);

	public void insertBrokerStatement(BrokerStatementHeader header) throws ODataServiceErrorException,ODataException {
        Map<String, List<BrokerStatementHeader>> data = new HashMap();
        List<BrokerStatementHeader> finalResults = new ArrayList<BrokerStatementHeader>();

        final HttpDestination httpDestination = DestinationAccessor.getDestination("brRecon")
        .asHttp().decorate(DefaultErpHttpDestination::new);

        logger.info("Http Destination Created for {}", "brRecon");
        final HttpClient client = HttpClientAccessor.getHttpClient(httpDestination);
        String query = "";
        TradingBrokerStatementService serv = new DefaultTradingBrokerStatementService();
        try{
        serv.createBrokerStatementHeader(header).execute(httpDestination);
        }
        catch(ODataException e){
            logger.error("Error response while sending request: {}", e);
            throw e;
        }

    }
    
    
}
