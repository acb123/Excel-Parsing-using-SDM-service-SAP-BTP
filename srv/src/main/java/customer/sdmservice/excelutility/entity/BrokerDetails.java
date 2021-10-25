package customer.sdmservice.excelutility.entity;

// import org.apache.poi.ss.usermodel.DateUtil;

// import java.util.Date;

// import org.apache.poi.hpsf.Date;

public class BrokerDetails extends AbstractDataModel {
    
    // private String  BrokerName;
	// private String  AccountNumber;
	// private String  StatementDate;
	private String  ProductCode;
	private Integer  NumberOfContracts;  
	private Integer  RemainingPosition;
    private Integer  ExercisedPosition;	
    private String  Direction;
    private Double	ContractPrice;  
    private Double	SettlementPrice;
    private String	Currency;
    private Double	StrikePrice; 
    private Integer	CallPut;
    private String 	TradeDate;
    private String	ExerciseDate;
    private String	ProductSymbol;
    private String  MaturityDate;
    private Integer	DerivativeCategory;
    private Integer	SettlementType	;
    private String  Type;


//     public String getBrokerName() {
//         return BrokerName;
//     } 
//   public void setBrokerName(String BrokerName) {
//    this.BrokerName = BrokerName;
//   }

//   public String getAccountNumber() {
//    return AccountNumber;
//   }
//   public void setAccountNumber(String AccountNumber) {
//    this.AccountNumber = AccountNumber;
//   }

//   public String getStatementDate() {
//    return StatementDate;
//   }
//   public void setStatementDate(String StatementDate) {
//    this.StatementDate = StatementDate;
//   }

  public String getProductCode() {
        return ProductCode;
  }
  public void setProductCode(String ProductCode) {
   this.ProductCode = ProductCode;
  }

  public Integer getNumberOfContracts() {
   return NumberOfContracts;
  }
  public void setNumberOfContracts(Integer NumberOfContracts) {
   this.NumberOfContracts = NumberOfContracts;
  }
 
  public Integer getRemainingPosition() {
   return RemainingPosition;
  }
  public void setRemainingPosition(Integer RemainingPosition) {
   this.RemainingPosition = RemainingPosition;
  }

  public Integer getExercisedPosition() {   
   return ExercisedPosition;
  }
  public void setExercisedPosition(Integer ExercisedPosition) {
   this.ExercisedPosition = ExercisedPosition;
  }

  public String getDirection() {
   return Direction;
  }
  public void setDirection(String Direction) {
   this.Direction = Direction;
  }

  public Double getContractPrice() {
   return ContractPrice;
  }
  public void setContractPrice(Double ContractPrice) {
   this.ContractPrice = ContractPrice;
  }

  public Double getSettlementPrice() {
   return SettlementPrice;
  }
  public void setSettlementPrice(Double SettlementPrice) {
    this.SettlementPrice = SettlementPrice;
  }

  public String getCurrency() {
   return Currency;
  }
  public void setCurrency(String Currency) {
    this.Currency = Currency;
  }
  
    public Double getStrikePrice() {
   return StrikePrice;
  }
  public void setStrikePrice(Double StrikePrice) {
    this.StrikePrice = StrikePrice;
  }

  public Integer getCallPut() {
   return CallPut;
  }
  public void setCallPut(Integer CallPut) {
    this.CallPut = CallPut;
  }

  public String getTradeDate() {
   return TradeDate;
  }
  public void setTradeDate(String TradeDate) {
    this.TradeDate = TradeDate;
  }

    public String getExerciseDate() {
   return ExerciseDate;
  }
  public void setExerciseDate(String ExerciseDate) {
    this.ExerciseDate = ExerciseDate;
  }

  public String getProductSymbol() {
   return ProductSymbol;
  }
  public void setProductSymbol(String ProductSymbol) {
    this.ProductSymbol = ProductSymbol;
  }

 public String getMaturityDate() {
   return MaturityDate;
  }
  public void setMaturityDate(String MaturityDate) {
    this.MaturityDate = MaturityDate;
  }

  public Integer getDerivativeCategory() {
   return DerivativeCategory;
  }
  public void setDerivativeCategory(Integer DerivativeCategory) {
    this.DerivativeCategory = DerivativeCategory;
  }

  public Integer getSettlementType() {
   return SettlementType;
  }
  public void setSettlementType(Integer SettlementType) {
    this.SettlementType = SettlementType;
  }

  public String getType() {
   return Type;
  }
  public void setType(String Type) {
    this.Type = Type;
  }

	// @Override
	// public String toString() {
	// 	return "Product [ ProductCode=" + ProductCode + ", NumberOfContracts=" + NumberOfContracts + ", RemainingPosition=" + RemainingPosition + ", ExrcisedPosition=" 
	// 			+ ExercisedPosition + ", Direct=" + Direct + ", ContractPrice=" + ContractPrice + ", SettlementPrice=" + SettlementPrice + ", Currency=" + Currency + ", Strike Price="
    //             + StrikePrice + ", CallPut=" + CallPut + ", TradeDate=" + TradeDate + ", ExerciseDate=" + ExerciseDate + ", ProductSymbol=" + ProductSymbol + 
    //              ", Derivative Category=" + DerivativeCategory +", SettlementType=" + SettlementType + ", Type=" + Type + "]";
	// }
    
    


}
