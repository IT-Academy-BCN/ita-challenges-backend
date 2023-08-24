package com.itachallenge.challenge.security.service;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.itachallenge.challenge.App;
import com.itachallenge.challenge.config.SecurityPropertiesConfig;
import com.itachallenge.challenge.security.service.exceptions.InvalidTokenException;
import com.itachallenge.challenge.security.service.exceptions.TokenExpiredException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class TokenService {

  
   private  SecurityPropertiesConfig config;
   

    public Mono<Claims> validateToken(String token) {
        return Mono.fromCallable(() -> {
            return Jwts.parserBuilder()
                    .setSigningKey(config.getSecret())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        })
        	      .onErrorResume(ExpiredJwtException.class, ex -> {
        	            return Mono.error(new TokenExpiredException("Token has expired"));
        	        })
        	        .onErrorResume(JwtException.class, ex -> {
        	            return Mono.error(new InvalidTokenException("Invalid token"));
        	        });
        	    }
    
    public Mono<Authentication> extractAuthenticationFromToken(String token) {
        return validateToken(token)
                .map(claims -> {
                	@SuppressWarnings("unchecked")
					List<String> authorities = (List<String>) claims.get("roles");     
                	List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    String username = claims.get("name").toString();

                    return new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                });
    }
}
