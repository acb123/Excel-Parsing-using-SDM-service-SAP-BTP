package customer.sdmservice.Model;

public class TriggerReconciliationResponse {

    private String startDate;
    private String endDate;
    private String accountNumber;
    private String brokerID;
    
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getBrokerID() {
        return brokerID;
    }
    public void setBrokerID(String brokerID) {
        this.brokerID = brokerID;
    }
}
