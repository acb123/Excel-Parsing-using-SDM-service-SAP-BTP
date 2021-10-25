package customer.sdmservice.Util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.HttpClient;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestinationProperties;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.datamodel.odata.client.ODataProtocol;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestFunction;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestRead;
import com.sap.cloud.sdk.datamodel.odata.client.request.ODataRequestResultGeneric;
import com.sap.cloud.sdk.datamodel.odatav4.core.CreateRequestBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.result.GsonResultObject;
import com.sap.cloud.sdk.result.ResultElement;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementHeader;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementTrade;
import com.sdmservice.vdm.namespaces.tradingbrokerstatement.BrokerStatementOptionExercise;
import com.sdmservice.vdm.services.TradingBrokerStatementService;
import com.sdmservice.vdm.services.DefaultTradingBrokerStatementService;



@WebServlet("/tradingContracts/1")
public class TradingContractServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    PersistenceService db;
    private static final Logger logger = CloudLoggerFactory.getLogger(TradingContractServlet.class);

    private static final String CATEGORY_PERSON = "1";


    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException{
        

        Map<String, List<BrokerStatementHeader>> data = new HashMap();
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
        }
        catch(ODataException e){
            logger.error("Error response while sending request");
        }

        //logger.info( "Type client Results {}" ,serv.createBrokerStatementHeader(header).execute(httpDestination));
        
    }

    
    private void setData(BrokerStatementHeader header){
        header.setBrokerID("DEUBA");
        //header.setBusinessPartnerID("xyz");
        header.setActivityFromDate(LocalDateTime.now());
        header.setActivityToDate(LocalDateTime.now());
        header.setAccountNumber("777777");
        header.setStatementDate(LocalDateTime.now());
        List<BrokerStatementTrade> trades = new ArrayList<>();
        header.setBrokerStatementTrade(trades);
        List<BrokerStatementOptionExercise> ops = new ArrayList<>();
        header.setBrokerStatementOptionExercise(ops);
    }
}


