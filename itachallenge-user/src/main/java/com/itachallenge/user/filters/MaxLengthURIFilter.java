package com.itachallenge.user.filters;

import com.itachallenge.user.config.PropertiesConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class MaxLengthURIFilter implements Filter {
    //region VARIABLES
    @Autowired
    PropertiesConfig prpsConfig;

    //endregion VARIABLES


    //region FILTERS
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //region VARIABLES
        int totalURLLength;
        HttpServletRequest requestHttp = (HttpServletRequest) request;
        HttpServletResponse responseHttp = (HttpServletResponse) response;

        //endregion VARIABLES


        //region ACTIONS
        // Calculate the max lengt, URL base (p.ex. "/itachallenge/api/v1/user/statistics")
        // and query (challenge=UUID_1&challenge=UUID_2&...)
        totalURLLength = requestHttp.getRequestURL().length() + requestHttp.getQueryString().length();
        if (prpsConfig.getUrlMaxLength() < totalURLLength) {
            // Too long
            responseHttp.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        }
        else {
            // Is good, continuous with the normal flux of the request
            chain.doFilter(request, response);
        }

        //endregion ACTIONS

    }

    //endregion FILTERS

}
