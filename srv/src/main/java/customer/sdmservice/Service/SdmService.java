package customer.sdmservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Arrays;

import com.sap.cloud.security.xsuaa.client.ClientCredentials;
import com.sap.cloud.security.xsuaa.client.XsuaaDefaultEndpoints;
import com.sap.cloud.security.xsuaa.client.XsuaaOAuth2TokenService;
import com.sap.cloud.security.xsuaa.tokenflows.TokenFlowException;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import io.pivotal.cfenv.core.CfEnv;
import io.pivotal.cfenv.core.CfService;

import org.apache.chemistry.opencmis.client.SessionParameterMap;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;


import org.springframework.web.client.RestTemplate;

import customer.sdmservice.Model.GetFileResponse;
import customer.sdmservice.Util.RepositoryConstants;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.http.HttpStatus;

import org.json.JSONObject;


@Service
public class SdmService {
   private final Logger logger = LoggerFactory.getLogger(SdmService.class);

  @Autowired
  private RestTemplate restTemplate;
    
  /**
   * Fetch SDM service by label from VCAP
   * @return CfService to connect
   */
	public CfService sdmCfService() {
		CfEnv cfEnv = new CfEnv();
		return cfEnv.findServiceByLabel("sdm");
    }

    /**
     * Generate JWT token to connect to SDM
     * @param subDomain Tenant SubDomain name
     * @return XsuaaTokenFlows Token to connect to SDM
     */
	public XsuaaTokenFlows sdmXsuaaTokenFlow(String subDomain) {
        CfService sdmCfService = sdmCfService();
		LinkedHashMap<String, String> xsuaaCredentials = (LinkedHashMap) sdmCfService.getCredentials().getMap().get("uaa");
        
        String uaadomain = xsuaaCredentials.get("uaadomain");

        String url = "https://" + subDomain + "." + uaadomain + "/oauth/token?grant_type=client_credentials";
        return new XsuaaTokenFlows(
				new XsuaaOAuth2TokenService(restTemplate),
				new XsuaaDefaultEndpoints(url),
		new ClientCredentials(xsuaaCredentials.get("clientid"), xsuaaCredentials.get("clientsecret")));
	}

    /**
     * Establish a session to connect to repository
     * @param subDomain Tenant SubDomain name
     * @return Session Repository Session
     */
	public Session cmisSession(String subDomain) throws TokenFlowException {
        CfService sdmCfService = sdmCfService();
        XsuaaTokenFlows sdmXsuaaTokenFlow = sdmXsuaaTokenFlow(subDomain);

        String sdmUaaToken = sdmXsuaaTokenFlow.clientCredentialsTokenFlow().execute().getAccessToken();
		String ecmServiceUrl = sdmCfService.getCredentials().getMap().get("uri").toString();

        // if (!isRepositoryAvailable(ecmServiceUrl, sdmUaaToken)) {
        //     createRepository(ecmServiceUrl, sdmUaaToken, subDomain);
        // }
		// default factory implementation
		SessionFactory factory = SessionFactoryImpl.newInstance();

		SessionParameterMap sessionParameters = new SessionParameterMap();
		sessionParameters.setBrowserBindingUrl(ecmServiceUrl + "browser");
		sessionParameters.setOAuthBearerTokenAuthentication(sdmUaaToken);

		List<Repository> repositories = factory.getRepositories(sessionParameters);

		return repositories.get(0).createSession();
    }
    
    /**
     * Check if repository is available
     * @param ecmServiceUrl SDM service URL
     * @param sdmUaaToken SDM JWT token
     * @return boolean If Repository available
     */
    public boolean isRepositoryAvailable(String ecmServiceUrl, String sdmUaaToken) {
		HttpHeaders headers = createOAuthBearerHeaders(sdmUaaToken);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		String result = restTemplate.exchange(ecmServiceUrl + RepositoryConstants.REPO_SERVICE_URL, HttpMethod.GET, entity, String.class)
                .getBody();

        JSONObject repositoryJsonObject = new JSONObject(result);
        logger.info("repositoryJsonObject "+repositoryJsonObject);
        return !(repositoryJsonObject == null || repositoryJsonObject.isEmpty());
    }

    /**
     * Create OAuth Headers
     */
    public HttpHeaders createOAuthBearerHeaders(String oauthToken) {
		return new HttpHeaders() {
			{
				String authHeader = "Bearer " + oauthToken;
				set("Authorization", authHeader);
			}
		};
    }

    public void getFiles(String subdomain) throws TokenFlowException, CmisObjectNotFoundException {
        Session session = cmisSession(subdomain); 
        Folder folder;
        try {
        folder = (Folder) session.getRootFolder();
        } catch (CmisObjectNotFoundException e) {
          throw new CmisObjectNotFoundException("Folder not found", e);
        }
        
        ItemIterable<CmisObject> children = folder.getChildren();

        System.out.println("Found the following objects in the root folder:-");
        for (CmisObject o : children) {
            System.out.println(o.getName() + "and ID: " + o.getId() + " which is of type " + o.getType().getDisplayName());
}
    }    


    /**
     * Move Object to either Success or Failed folder
     * Depending on the parsing success
     * @param subDomain
     * @param status
     * @param fileResponse
     * @throws IOException
     */
   public void moveObject(String subDomain,HttpStatus status,GetFileResponse fileResponse) throws IOException {
        Session session = cmisSession(subDomain);
        Document document = null;
        // List<Document> documents = new ArrayList<Document>();
        Folder folder,fTargetFolder,sTargetFolder;
        try {
        folder = (Folder) session.getRootFolder();
        sTargetFolder = (Folder) session.getObjectByPath("/", "Archive-Success");
        fTargetFolder = (Folder) session.getObjectByPath("/", "Archive-Failed");
        ItemIterable<CmisObject> children = folder.getChildren();
         for(CmisObject o:children){
/**Fetch only documents */
            if(o.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT) 
                && o.getName().equals(fileResponse.getFileName())){
             document = (Document) session.getObject(o.getId());
             Map<String, String> properties = new HashMap<>();
             properties.put(PropertyIds.NAME, getModifiedName(document));
             properties.put(PropertyIds.LAST_MODIFIED_BY, getLastModifiedBy(document));
             document = (Document) document.rename(getModifiedName(document));
            //  FileableCmisObject fileableCmisObject = (FileableCmisObject) document;
             if(status.equals(HttpStatus.ACCEPTED)){
             document.move(folder, sTargetFolder);
             logger.info("FileName" + o.getName());
             logger.info("Document moved to Archive-Success folder");
             }
             else if(status.equals(HttpStatus.INTERNAL_SERVER_ERROR)){
             document.move(folder, fTargetFolder);
             logger.info("FileName" + o.getName());
             logger.info("Document moved to Archive-Failed folder error");
             }
            
            }
         }
        }catch (CmisObjectNotFoundException e) {
          throw new CmisObjectNotFoundException("Document Not Moved", e);
        }
   }

    private String getModifiedName(Document document) {
        String time = LocalDateTime.now().toString();
         String name = document.getName();
         int index = name.lastIndexOf(".");
         String newName = name.substring(0,index).concat("_") + time.concat(name.substring(index));
        return newName;
    }

    private String getLastModifiedBy(Document document) {
        String modifiedBy = document.getLastModifiedBy();
         if(StringUtils.isEmpty(modifiedBy)){
             modifiedBy = document.getCreatedBy();
         }
        return modifiedBy;
    }

    /**
     * Read File from Document Management Service, if exists.
     * Otherwise, an exception CmisObjectNotFoundException is thrown
     * 
     * @param subDomain Tenant SubDomain name
	 * @param documentId the ObjectId of the document to be downloaded
	 * @return {@link GetFileResponse} object
     * @throws IOException
     */
   public List<GetFileResponse> getFileInputStream(String subDomain) throws IOException {
        Session session = cmisSession(subDomain);
        Document document = null;
        // List<Document> documents = new ArrayList<Document>();
        List<GetFileResponse> getFileResponses = new ArrayList<GetFileResponse>();
        Folder folder;
        try {
        folder = (Folder) session.getRootFolder();
        ItemIterable<CmisObject> children = folder.getChildren();
         for(CmisObject o:children){
             if(o.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)){
             document = (Document) session.getObject(o.getId());
            //  documents.add(document);
            //  break;
             String fileName = document.getName();
             String contentType = document.getContentStreamMimeType();

             String  createdBy = document.getCreatedBy();
             String changedBy = document.getLastModifiedBy();
             Instant createdAt = document.getCreationDate().toInstant();
             Instant changedAt = document.getLastModificationDate().toInstant();


             InputStream inputStream = document.getContentStream().getStream();
             getFileResponses.add(new GetFileResponse( document.getId(), fileName, contentType, inputStream, createdBy, changedBy,createdAt,changedAt));
             }
         }
        } catch (CmisObjectNotFoundException e) {
          throw new CmisObjectNotFoundException("Document not found", e);
        }

        return getFileResponses;
  }


  /**
     * Delete document from Document Management Service, if exists.
     * Otherwise, an exception CmisObjectNotFoundException is thrown
     * 
     * @param subDomain Tenant SubDomain name
	 * @param documentId the ObjectId of the document to be deleted
     */
  public void deleteDocument(String subDomain, String documentId) throws TokenFlowException {
        Session session = cmisSession(subDomain);
        Document document;
        try {
        document = (Document) session.getObject(documentId);
        } catch (CmisObjectNotFoundException e) {
          throw new CmisObjectNotFoundException("Document not found", e);
        }
   
        document.delete();
  }


}
