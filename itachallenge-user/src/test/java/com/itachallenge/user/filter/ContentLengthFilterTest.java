package com.itachallenge.user.filter;

import com.itachallenge.user.config.TomcatConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ContentLengthFilterTest {

    @Mock
    private TomcatConfig tomcatConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ContentLengthFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilter_whenContentLengthExceedsLimit_thenThrowServletException() {
        when(request.getContentLength()).thenReturn(100);
        when(tomcatConfig.getMaxHttpFormPostSize()).thenReturn(50);

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, filterChain));
    }

    @Test
    void doFilter_whenContentLengthIsWithinLimit_thenProceedWithChain() throws ServletException, IOException {
        when(request.getContentLength()).thenReturn(30);
        when(tomcatConfig.getMaxHttpFormPostSize()).thenReturn(50);

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}