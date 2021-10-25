package customer.sdmservice.excelutility.controllers;

import org.springframework.context.annotation.Configuration;
// import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
 

@Configuration
@EnableWebSecurity
class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
 
 @Override
 public void configure(HttpSecurity http) throws Exception {
 // To test the excel parsing functionality, I have pushed as of now. will revert.
 /*
 * http
 .authorizeRequests().antMatchers("/actuator/health").permitAll()
 .anyRequest().authenticated()
 .and()
 */
 http
 .authorizeRequests().antMatchers("/**").permitAll()
 .and()
 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
 .and()
 .httpBasic().disable()
 .formLogin().disable()
 .csrf().disable();
 }
 
}