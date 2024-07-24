package com.itachallenge.user.filter;

import com.itachallenge.user.config.TomcatConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ContentLengthFilter implements Filter {

    private final TomcatConfig tomcatConfig;

    @Autowired
    public ContentLengthFilter(TomcatConfig tomcatConfig) {
        this.tomcatConfig = tomcatConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            int contentLength = request.getContentLength();
            if (contentLength > tomcatConfig.getMaxHttpFormPostSize()) {
                throw new ServletException("Request body is too large!");
            }
        }
        chain.doFilter(request, response);
    }

}
