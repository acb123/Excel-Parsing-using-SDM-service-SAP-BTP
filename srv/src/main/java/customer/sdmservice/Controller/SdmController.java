package customer.sdmservice.Controller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.*;

import javax.servlet.http.HttpServletRequest;

import com.sap.cloud.security.xsuaa.token.AuthenticationToken;
import com.sap.cloud.security.xsuaa.token.XsuaaToken;
import com.sap.cloud.security.xsuaa.tokenflows.TokenFlowException;

import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;

import customer.sdmservice.Model.GetFileResponse;
import customer.sdmservice.Service.SdmService;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
public class SdmController {

   private final Logger logger = LoggerFactory.getLogger(SdmController.class);

    public static final String SDM_UPLOAD_RELATIVE_PATH_V1 = "/upload";

     public static final String ENTITY_TYPE = "/{entityType}";

    @Autowired
    private SdmService sdmService;

    
    /**
     * Download Document from Document Management Service, if exists.
     * Otherwise, an exception CmisObjectNotFoundException is thrown
     * @param subDomain Tenant SubDomain name
	 * @param documentId the ObjectId of the document to be downloaded
	 * @return ResponseEntity with HTTP status,headers and body
     */
    @GetMapping("/contents")
	public void downloadDocument(@RequestParam String subDomain) throws TokenFlowException {
        try {
           sdmService.getFiles(subDomain);
        } catch (CmisObjectNotFoundException e) {
            System.out.println("Documents not found");
        } 
    }

    
    /**
     * Download Document from Document Management Service, if exists.
     * Otherwise, an exception CmisObjectNotFoundException is thrown
     * @param subDomain Tenant SubDomain name
	 * @return ResponseEntity with HTTP status,headers and body
     */
    @GetMapping("/fileData")
	public List<GetFileResponse> GetFileData(@RequestParam String subDomain) throws TokenFlowException {
        try {
          return sdmService.getFileInputStream(subDomain);
        } catch (CmisObjectNotFoundException e) {
            List<GetFileResponse> fileResponses = new ArrayList<>();
            fileResponses.add(new GetFileResponse(null, null, null, null,null, null, null, null));
            return fileResponses;
        //    return new  GetFileResponse(null, null, null, null);
        } catch ( IOException i ){
            List<GetFileResponse> fileResponses = new ArrayList<>();
            fileResponses.add(new GetFileResponse(null, null, null, null,null, null, null, null));
            return fileResponses;
            // return new GetFileResponse(null, null, null, null);
        }
    }

    // @GetMapping("/movement")
    // public ResponseEntity<String> moveFile(@RequestParam String subDomain) throws IOException {
    //     try{
    //        sdmService.moveObject(subDomain,HttpStatus.ACCEPTED);
    //         return errorMessage("Document moved to folder", HttpStatus.ACCEPTED);
    //     }catch (CmisObjectNotFoundException c){
    //         return errorMessage("Failure to move document", HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    // }
    // /**
    //  * Delete Document from Document Management Service, if exists.
    //  * Otherwise, an exception CmisObjectNotFoundException is thrown
    //  * 
    //  * @param subDomain Tenant SubDomain name
	//  * @param documentId the ObjectId of the document to be deleted
	//  * @return ResponseEntity with HTTP status,headers and body
    //  */
    // @GetMapping("/content/v1/delete")
	// public ResponseEntity<String> deleteDocument(@RequestParam String subDomain, @RequestParam String documentId) throws TokenFlowException {
    //      try {
    //       sdmService.deleteDocument(subDomain, documentId);

    //       return new ResponseEntity<String>("Document: "+documentId + " deleted successfully", HttpStatus.ACCEPTED);
    //     } catch (CmisObjectNotFoundException e) {
    //         return errorMessage("Document ID: " + documentId + " not found", HttpStatus.NOT_FOUND);
    //     } 
    // }
    
    // /**
    //  * Upload Document to Document Management Service.
    //  * FileName has to be unique as per Apache Chemistry,
    //  * Otherwise CmisNameConstraintViolationException is thrown
    //  * 
    //  * @param subDomain Tenant SubDomain name
	//  * @param documentId the ObjectId of the document to be deleted
	//  * @return ResponseEntity with HTTP status,headers and body
    //  */
    // @PostMapping(path = "/content/v1/{entityType}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // public ResponseEntity<String> uploadDocument(@PathVariable String entityType,
    // HttpServletRequest request) throws IOException {
    //     MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

    //     MultipartFile multipartFile = multipartRequest.getFile("file");
        
    //     AuthenticationToken authenticationToken = (AuthenticationToken) request.getUserPrincipal();
         
    //     XsuaaToken xsuaaToken = (XsuaaToken) authenticationToken.getPrincipal();

    //     String originalFilename = multipartFile.getOriginalFilename();
    //     String encodedFilename = URLEncoder.encode(originalFilename, "utf-8");

    //     byte[] arrayByte = multipartFile.getBytes();

    //     InputStream inputStream = new ByteArrayInputStream(arrayByte);
    //     byte[] bytes =null;   
    //     Boolean validExcel = true;
    //     try{
    //         ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //         IOUtils.copy(inputStream, baos);
    //         bytes = baos.toByteArray();
       
    //         ByteArrayInputStream input1 = new ByteArrayInputStream(bytes);
    //         input1.reset();
    //         logger.info("Calling  excelSheetValidation");
    //         validExcel  = excelValidationSevice.excelSheetValidation(input1);
    //     } catch (Exception e) {
    //         logger.error("Error : Eception " + e );
    //     } 
    //     if(!validExcel){
    //        return errorMessage("Document with name " + encodedFilename + " is Invalid. Please correct and upload again.", HttpStatus.INTERNAL_SERVER_ERROR);
    //     }
    
    //     ByteArrayInputStream streamInput = new ByteArrayInputStream(bytes);
    //         streamInput.reset();

    //     String documentId = "";

    //     try {
    //         documentId = sdmService.putFile(xsuaaToken, entityType, encodedFilename, multipartFile.getContentType(), streamInput, arrayByte.length);
    //         return new ResponseEntity<String>(documentId, HttpStatus.ACCEPTED);
    //     } catch (CmisNameConstraintViolationException e) {
    //         return errorMessage("Document with name " + encodedFilename + " already exists", HttpStatus.INTERNAL_SERVER_ERROR);
    //     } 
    // }
    
    /**
	 * Helper function to form the responseEntity
     * @param message
	 * @param status
	 * @return ResponseEntity with HTTP status,headers and body
	 */
	private static ResponseEntity errorMessage(String message, HttpStatus status) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);

		return ResponseEntity.status(status).headers(headers).body(message);
	}
}
