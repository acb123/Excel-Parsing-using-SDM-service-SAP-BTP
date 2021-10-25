package customer.sdmservice.excelutility.controllers;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import customer.sdmservice.excelutility.service.DataParsingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sap.cloud.sdk.datamodel.odata.client.exception.ODataServiceErrorException;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class ExcelParsingController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataParsingService dts;

    @RequestMapping(value = "/parsedExcel", method = RequestMethod.GET) //, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<?> processExcelData(@RequestParam String subDomain) throws Exception { 

        try{
        dts.parseExcelData(subDomain); 
        return logMessage("Documents have been Parsed, Persisted and moved to Archive-Success Folder", HttpStatus.ACCEPTED);
        }catch(CmisObjectNotFoundException c){
        
         return logMessage("Document parsing failed ", HttpStatus.INTERNAL_SERVER_ERROR);
        }catch(ODataServiceErrorException e){
            

            logger.info("The OData service responded with an error: {}", e.getOdataError());
            List<String> oDMS = new ArrayList<>();
            int s = e.getOdataError().getDetails().size();
            for(int i = 0;i < s; i++){
                oDMS.add(e.getOdataError().getDetails().get(i).getODataMessage().toString());
            }
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(oDMS);//e.getOdataError().getODataMessage()
        
        }catch(IOException io){
           return logMessage("Document moved to Archive-Failed Folder", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
	 * Helper function to form the responseEntity
     * @param message
	 * @param status
	 * @return ResponseEntity with HTTP status,headers and body
	 */
	private static ResponseEntity<?> logMessage(String message, HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
   
		return ResponseEntity.status(status).headers(headers).body(message);
	}

}
