package reservations.journey_planner.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /*
    @Bean
    public JwtDecoder decoder(){
        return NimbusJwtDecoder.withJwkSetUri(jwtUrl).build();
    }

     */

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> authenticationConverter() {
        return new JwtAuthenticationConverter();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
     /*   http.authorizeRequests().antMatchers("/").permitAll();
        http.csrf().disable();*/
        http.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/cities/**").permitAll()
                .antMatchers("/reservations/**").permitAll().
                antMatchers("/routes/**").permitAll().
                antMatchers("/trains/**").permitAll().
                antMatchers("/stations/**").permitAll().
                antMatchers("/seats/**").permitAll().
                anyRequest().authenticated().and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(authenticationConverter())
        ;
    }
}
