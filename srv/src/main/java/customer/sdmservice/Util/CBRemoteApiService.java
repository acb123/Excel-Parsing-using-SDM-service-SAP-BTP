package customer.sdmservice.Util;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
import com.sdmservice.vdm.namespaces.uploadclearingtradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.services.UploadClearingTradingBrokerStatementService;
import com.sdmservice.vdm.services.DefaultUploadClearingTradingBrokerStatementService;

@Component
public class CBRemoteApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public void insertBrokerStatement(BrokerStatementHeader header) throws ODataServiceErrorException,ODataException {

        final HttpDestination httpDestination = DestinationAccessor.getDestination("brReconCB")
        .asHttp().decorate(DefaultErpHttpDestination::new);

        logger.info("Http Destination Created for {}", "brReconCB");
        UploadClearingTradingBrokerStatementService serv = new DefaultUploadClearingTradingBrokerStatementService();
        try{
        serv.createBrokerStatementHeader(header).execute(httpDestination);
        }
        catch(ODataException e){
            logger.error("Error response while sending request: {}", e);
            throw e;
        }

    }
    
}

