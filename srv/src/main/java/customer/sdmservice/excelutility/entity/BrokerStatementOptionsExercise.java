package customer.sdmservice.excelutility.entity;

// import java.util.Date;
import java.time.LocalDate;
import java.util.Currency;
import lombok.Data;

@Data
public class BrokerStatementOptionsExercise extends AbstractDataModel{
    
    private BrokerHeader brokerHeader;
    private String productCode;
    private Double strikePrice;
    private Integer putCallIndicator;
    private Integer totalPosition;
    private Integer remainingPosition;
    private Integer exercisedPosition;
    private String tradeDirection;
    private Double settlementPrice;
    private Double premium;
    private Currency currency;
    private LocalDate exerciseDate;
    private String productSymbol;
    private LocalDate maturityDate;
    private Integer settlementType;

    
    public Integer getRemainingPosition() {
        return remainingPosition;
    }
    public void setRemainingPosition(Integer remainingPosition) {
        this.remainingPosition = remainingPosition;
    }

    public Integer getSettlementType() {
        return settlementType;
    }
    public void setSettlementType(Integer settlementType) {
        this.settlementType = settlementType;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getProductSymbol() {
        return productSymbol;
    }
    public void setProductSymbol(String productSymbol) {
        this.productSymbol = productSymbol;
    }
    
    public LocalDate getExerciseDate() {
        return exerciseDate;
    }
    public void setExerciseDate(LocalDate exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Double getPremium() {
        return premium;
    }
    public void setPremium(Double premium) {
        this.premium = premium;
    }

    public Double getSettlementPrice() {
        return settlementPrice;
    }
    public void setSettlementPrice(Double settlementPrice) {
        this.settlementPrice = settlementPrice;
    }

    public String getTradeDirection() {
        return tradeDirection;
    }
    public void setTradeDirection(String tradeDirection) {
        this.tradeDirection = tradeDirection;
    }

    public Integer getExercisedPosition() {
        return exercisedPosition;
    }
    public void setExercisedPosition(Integer exercisedPosition) {
        this.exercisedPosition = exercisedPosition;
    }

    public Integer getTotalPosition() {
        return totalPosition;
    }
    public void setTotalPosition(Integer totalPosition) {
        this.totalPosition = totalPosition;
    }

    public Integer getPutCallIndicator() {
        return putCallIndicator;
    }
    public void setPutCallIndicator(Integer putCallIndicator) {
        this.putCallIndicator = putCallIndicator;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }
    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BrokerHeader getBrokerHeader() {
        return brokerHeader;
    }
    public void setBrokerHeader(BrokerHeader brokerHeader) {
        this.brokerHeader = brokerHeader;
    }

    
}
