package customer.sdmservice.Model;

import java.io.InputStream;
import java.time.Instant;

// import org.apache.chemistry.opencmis.commons.data.ContentStream;

// import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Downloaded File properties
 */
// @AllArgsConstructor
@Data
public class GetFileResponse {
    private String id;
	private String fileName;
    private String contentType;
    // private String stream;
    // private ContentStream stream;
    private InputStream stream;
    private String createdBy;
    private String changedBy;
    private Instant createdAt;
    private Instant changedAt;

 
    public String getId() {
        return this.id;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public InputStream getStream() {
        return this.stream;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }
    
    public String getChangedBy() {
        return this.changedBy;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
    
    public Instant getChangedAt() {
        return this.changedAt;
    }

    public void setId(final String id) {
        this.id = id;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }
    
    public void setStream(final InputStream stream) {
        this.stream = stream;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }
    
    public void setChangedBy(final String changedBy) {
        this.changedBy = changedBy;
    }

    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setChangedAt(final Instant changedAt) {
        this.changedAt = changedAt;
    }

     public GetFileResponse(final String id, final String fileName, final String contentType, final InputStream stream,
                            final String createdBy, final String changedBy,final Instant createdAt,final Instant changedAt) {
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.stream = stream;
        this.createdBy = createdBy;
        this.changedBy = changedBy;
        this.createdAt = createdAt;
        this.changedAt = changedAt;
    }

}
