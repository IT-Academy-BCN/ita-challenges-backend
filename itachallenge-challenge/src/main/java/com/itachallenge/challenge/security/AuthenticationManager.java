package com.itachallenge.challenge.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.itachallenge.challenge.security.service.TokenService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Lazy
@Component
//@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    //private final Builder webClient;
    
    @Autowired
    public  TokenService tokenservice;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();
        return tokenservice.extractAuthenticationFromToken(jwtToken);
        		
                
    }

    /*private UsernamePasswordAuthenticationToken getAuthorities(UserAuthorities userAuthorities) {
        return new UsernamePasswordAuthenticationToken(
                userAuthorities.getUsername(), null,
                userAuthorities.getAuthorities().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
    }*/
    


    /*private ResponseSpec tokenValidate(String token) {
        return webClient.build()
                .get()
                .uri(uriBuilder -> uriBuilder.host("registration").path("/token/auth").queryParam("token", token).build())
                .retrieve()
                .onStatus(HttpStatus.FORBIDDEN::equals, response -> Mono.error(new IllegalStateException("Token is not valid")));
    }*/
}
