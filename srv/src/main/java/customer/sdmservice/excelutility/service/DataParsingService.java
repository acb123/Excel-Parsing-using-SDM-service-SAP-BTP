package customer.sdmservice.excelutility.service;


public interface DataParsingService {
    
    /**
     * This is the main method for Parsing of Trading broker and Clearing Broker Statements in Excel format
     * @param subDomainID
     * @throws Exception
     */
    public void parseExcelData(final String subDomainID) throws Exception;
    


}
