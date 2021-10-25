package customer.sdmservice.excelutility.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Currency;
// import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class BrokerStatementTrade extends AbstractDataModel{

    private Integer noOfContracts;
    private Double contractPrice;
    private Double strikePrice;
    private Instant tradeDate;
    private LocalDate maturityDate;
    private String productCodeExchange;
    private Currency currency;
    private String productSymbol;
    private Integer putCallIndicator;
    private Integer derivativeCategory;
    private String direction;
    private BrokerHeader brokerHeader;

    public String getDirection() {
        return direction;
    }
    public void setDirection(String direction) {
        this.direction = direction;
    }
    public BrokerHeader getBrokerStatement() {
        return brokerHeader;
    }
    public void setBrokerStatement(BrokerHeader brokerHeader) {
        this.brokerHeader = brokerHeader;
    }
    public Integer getDerivativeCategory() {
        return derivativeCategory;
    }
    public void setDerivativeCategory(Integer derivativeCategory) {
        this.derivativeCategory = derivativeCategory;
    }

    public Integer getPutCallIndicator() {
        return putCallIndicator;
    }
    public void setPutCallIndicator(Integer putCallIndicator) {
        this.putCallIndicator = putCallIndicator;
    }

    public String getProductSymbol() {
        return productSymbol;
    }
    public void setProductSymbol(String productSymbol) {
        this.productSymbol = productSymbol;
    }

    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getProductCodeExchange() {
        return productCodeExchange;
    }
    public void setProductCodeExchange(String productCode) {
        this.productCodeExchange = productCode;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }
    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public Instant getTradeDate() {
        return tradeDate;
    }
    public void setTradeDate(Instant tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Double getStrikePrice() {
        return strikePrice;
    }
    public void setStrikePrice(Double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public Double getContractPrice() {
        return contractPrice;
    }
    public void setContractPrice(Double contractPrice) {
        this.contractPrice = contractPrice;
    }

    public Integer getNoOfContracts() {
        return noOfContracts;
    }
    public void setNoOfContracts(Integer noOfContracts) {
        this.noOfContracts = noOfContracts;
    }

}
