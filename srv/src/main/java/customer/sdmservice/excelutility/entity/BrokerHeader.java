package customer.sdmservice.excelutility.entity;

import java.time.Instant;
import java.time.LocalDate;
// import java.util.Date;
import java.util.List;

public class BrokerHeader {
    private String brokerID;
    private String AccountNumber;
    private LocalDate StatementDate;
    private LocalDate ActivityFromDate;
    private LocalDate ActivityToDate;
    private String createdBy;
    private String changedBy;
    private Instant createdAt;
    private Instant changedAt;
    private String statementUploadedBy;
    private String statementType;

    // private List<BrokerDetails> brokerDetails;
    private List<BrokerStatementTrade> brokerStatementTrades;
    private List<BrokerStatementOptionsExercise> brokerStatementOptionsExercises;

    public String getStatementType() {
        return statementType;
    }
    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }
    public void setBrokerStatementOptionsExercises(List<BrokerStatementOptionsExercise> brokerStatementOptionsExercises) {
        this.brokerStatementOptionsExercises = brokerStatementOptionsExercises;
    }
    public String getBrokerID() {
        return brokerID;
    }
    public void setBrokerID(String brokerID) {
        this.brokerID = brokerID;
    }

  public String getAccountNumber() {
   return AccountNumber;
  }
  public void setAccountNumber(String AccountNumber) {
   this.AccountNumber = AccountNumber;
  }

  public LocalDate getStatementDate() {
   return StatementDate;
  }
  public void setStatementDate(LocalDate StatementDate) {
   this.StatementDate = StatementDate;
  }

  public LocalDate getActivityFromDate(){
      return ActivityFromDate;
  }
   public void setActivityFromDate(LocalDate ActivityFromDate) {
   this.ActivityFromDate = ActivityFromDate;
  }
 
   public LocalDate getActivityToDate(){
      return ActivityToDate;
  }
   public void setActivityToDate(LocalDate ActivityToDate) {
    this.ActivityToDate = ActivityToDate;
  }

    public String getCreatedBy() {
        return this.createdBy;
    }
     public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getChangedBy() {
        return this.changedBy;
    }
    public void setChangedBy(final String changedBy) {
        this.changedBy = changedBy;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(final Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getChangedAt() {
        return this.changedAt;
    }
    public void setChangedAt(final Instant changedAt) {
        this.changedAt = changedAt;
    }

  public List<BrokerStatementTrade> getBrokerStatementTrades(){
      return brokerStatementTrades;
  }
  public void setBrokerStatementTrades(List<BrokerStatementTrade> brokerStatementTrades){
      this.brokerStatementTrades = brokerStatementTrades;
  }

  public List<BrokerStatementOptionsExercise> getBrokerStatementOptionsExercises(){
      return brokerStatementOptionsExercises;
  }
  public void setBrokerStatementOptionExercises(List<BrokerStatementOptionsExercise> brokerStatementOptionsExercises){
      this.brokerStatementOptionsExercises = brokerStatementOptionsExercises;
  }
public String getStatementUploadedBy() {
    return statementUploadedBy;
}
public void setStatementUploadedBy(String statementUploadedBy) {
    this.statementUploadedBy = statementUploadedBy;
}

}
