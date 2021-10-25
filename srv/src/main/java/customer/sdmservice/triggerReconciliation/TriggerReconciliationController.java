package customer.sdmservice.triggerReconciliation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.cloudplatform.logging.CloudLoggerFactory;
import com.sap.cloud.sdk.s4hana.connectivity.DefaultErpHttpDestination;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import customer.sdmservice.Model.TriggerReconciliationRequest;
import customer.sdmservice.Model.TriggerReconciliationResponse;

@RestController
public class TriggerReconciliationController {

    private static final Logger logger = CloudLoggerFactory.getLogger(TriggerReconciliationController.class);

    DateTimeFormatter requestFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    DateTimeFormatter responseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping("/triggerReconciliation")
	public ResponseEntity<?> triggerReconciliation(@RequestBody TriggerReconciliationRequest request) {
        
        final HttpDestination httpDestination = DestinationAccessor.getDestination("brRecon")
        .asHttp().decorate(DefaultErpHttpDestination::new);

        logger.info("Http Destination Created for {}", "brRecon");
        final HttpClient client = HttpClientAccessor.getHttpClient(httpDestination);

        HttpPost post = new HttpPost("jobScheduler");
        TriggerReconciliationResponse response = new TriggerReconciliationResponse();
        response.setEndDate((LocalDate.parse(request.getStatementDateRange().getEndDate(), requestFormatter)).format(responseFormatter));
        response.setStartDate((LocalDate.parse(request.getStatementDateRange().getStartDate(), requestFormatter)).format(responseFormatter));
        response.setAccountNumber(request.getAccountNumber());
        response.setBrokerID(request.getBrokerID());
        try {
            Gson gson = new Gson();    
            String json = gson.toJson(response);
            System.out.println(json);
            StringEntity entity = new StringEntity(json);
            post.setEntity(entity);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");
            HttpResponse resp = client.execute(post);
        } catch (ClientProtocolException e) {
            logger.error("Error response while sending request: {}", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error("Error response while sending request: {}", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    

}
