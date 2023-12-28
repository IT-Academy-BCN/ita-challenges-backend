package com.itachallenge.challenge.security;

import com.itachallenge.challenge.config.SecurityPropertiesConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityPropertiesConfig config;

    private final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String authorizationHeader = request.getHeader(config.getHeaderString());
            if(authorizationHeaderIsValid(authorizationHeader)) {
                Claims claims = validateToken(request);
                setUpSpringAuthentication(claims);
            }
            filterChain.doFilter(request, response);
        } catch(ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private boolean authorizationHeaderIsValid(String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX);
    }

    private Claims validateToken(HttpServletRequest request) {
        String jwtToken = request.getHeader(config.getHeaderString()).replace(TOKEN_PREFIX, "").trim();
        return Jwts.parserBuilder()
                .setSigningKey(config.getSecret().getBytes())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>)claims.get(config.getAuthoritiesClaim());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }


}