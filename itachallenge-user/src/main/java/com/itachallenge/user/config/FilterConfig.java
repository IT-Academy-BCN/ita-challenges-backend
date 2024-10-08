package com.itachallenge.user.config;

import com.itachallenge.user.filter.ContentLengthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContentLengthFilter> contentLength(TomcatConfig tomcatConfig) {
        FilterRegistrationBean<ContentLengthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new ContentLengthFilter(tomcatConfig));
        registrationBean.addUrlPatterns("/itachallenge/api/v1/user/solution/*");
        return registrationBean;
    }
}
