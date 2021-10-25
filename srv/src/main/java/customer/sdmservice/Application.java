package customer.sdmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;

import org.springframework.web.client.RestTemplate;

import customer.sdmservice.excelutility.entity.BrokerDetails;
import customer.sdmservice.excelutility.service.DataParsingService;
import customer.sdmservice.excelutility.service.impl.DataParsingServiceImpl;

@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootApplication
@ComponentScan(basePackages = {"customer.sdmservice","customer.sdmservice.excelutility"})
public class Application {

    //  private final Logger logger = LoggerFactory.getLogger(Application.class);

    // @Autowired
    //  ApplicationContextProvider applicationContextProvider;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

     @Bean
   	public RestTemplate restTemplate() {
   		return new RestTemplate();
       }
       
       @Bean
       public DataParsingService dts() {
           return new DataParsingServiceImpl();
       }

      @Bean
       public BrokerDetails BrD() {
           return new BrokerDetails();
       }

}
