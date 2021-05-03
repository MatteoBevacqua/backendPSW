package reservations.journey_planner.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/").permitAll();
        http.csrf().disable();
        /* http.csrf().disable()...sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/customers/**").permitAll()
                .antMatchers("/products/**").permitAll()
                anyRequest().authenticated().and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(authenticationConverter()).decoder(decoder())*/
        ;
    }
}
