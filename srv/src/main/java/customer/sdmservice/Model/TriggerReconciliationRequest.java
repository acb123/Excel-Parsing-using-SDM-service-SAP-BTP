package customer.sdmservice.Model;

public class TriggerReconciliationRequest {
    
    StatementDateRange statementDateRange;
    String brokerID;
    String accountNumber;
    
    public class StatementDateRange {

        String startDate;
        String endDate;
        
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
    }

    public StatementDateRange getStatementDateRange() {
        return statementDateRange;
    }

    public void setStatementDateRange(StatementDateRange statementDateRange) {
        this.statementDateRange = statementDateRange;
    }

    public String getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(String brokerID) {
        this.brokerID = brokerID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
}
