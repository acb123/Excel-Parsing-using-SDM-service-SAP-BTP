package customer.sdmservice.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

/**
 * Repository properties
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "displayName", "description", "repositoryType", "isVersionEnabled",  "isVirusScanEnabled",  "skipVirusScanForLargeFile",  "hashAlgorithms" })

public class OnboardingRepositoryDetail {
    
    @JsonProperty("displayName")
	private String displayName;
	@JsonProperty("description")
	private String description;
	@JsonProperty("repositoryType")
	private String repositoryType;
	@JsonProperty("isVersionEnabled")
	private String isVersionEnabled;
	@JsonProperty("isVirusScanEnabled")
	private String isVirusScanEnabled;
	@JsonProperty("skipVirusScanForLargeFile")
	private String skipVirusScanForLargeFile;
	@JsonProperty("hashAlgorithms")
	private String hashAlgorithms;
}
