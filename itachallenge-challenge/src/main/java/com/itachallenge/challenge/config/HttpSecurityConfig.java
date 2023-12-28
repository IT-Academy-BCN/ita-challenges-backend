package com.itachallenge.challenge.config;

import com.itachallenge.challenge.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;


import java.io.IOException;

@Configuration
public class HttpSecurityConfig {

    @Profile("dev")
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    @EnableWebSecurity
    public static class DisableSecurityConfig {
        @Bean
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().permitAll();
        }
    }

    @Profile("pro")
    @EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
    @EnableWebSecurity
    public static class WebSecurityConfig {


        private final SecurityPropertiesConfig config;
        private final JwtAuthenticationFilter jwtFilter;
        @Autowired
        public WebSecurityConfig(SecurityPropertiesConfig config, JwtAuthenticationFilter jwtFilter) {
            this.config = config;
            this.jwtFilter = jwtFilter;
        }

        @Bean
        public void configure(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable());
            http.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            http.authorizeRequests().anyRequest().authenticated();
            http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                    new BasicAuthenticationEntryPoint() {
                        @Override
                        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
                                throws IOException {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().print(config.getErr());
                        }
                    }
            ));
        }
    }
}
