package com.itachallenge.user.filter;

import com.itachallenge.user.config.PropertiesConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
@RequiredArgsConstructor
public class MaxLengthURIFilter implements Filter {

    private final PropertiesConfig prpsConfig;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        int totalURLLength;
        HttpServletRequest requestHttp = (HttpServletRequest) request;
        HttpServletResponse responseHttp = (HttpServletResponse) response;

        StringBuffer requestURL = requestHttp.getRequestURL();
        String queryString = requestHttp.getQueryString();

        int urlLength = (requestURL != null) ? requestURL.length() : 0;
        int queryStringLength = (queryString != null) ? queryString.length() : 0;

        totalURLLength = urlLength + queryStringLength;

        if (prpsConfig.getUrlMaxLength() < totalURLLength) {
            responseHttp.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        } else {
            chain.doFilter(request, response);
        }

    }

}
