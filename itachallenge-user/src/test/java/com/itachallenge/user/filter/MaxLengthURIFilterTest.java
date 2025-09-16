package com.itachallenge.user.filter;

import com.itachallenge.user.config.PropertiesConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class MaxLengthURIFilterTest {

    @Mock
    private PropertiesConfig prpsConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private MaxLengthURIFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilter_whenURILengthExceedsLimit_thenSetStatusToRequestURITooLong() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/some/path"));
        when(request.getQueryString()).thenReturn("query=long_string_that_exceeds_limit");
        when(prpsConfig.getUrlMaxLength()).thenReturn(50);

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilter_whenURILengthIsWithinLimit_thenProceedWithChain() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/some/path"));
        when(request.getQueryString()).thenReturn("query=ok");
        when(prpsConfig.getUrlMaxLength()).thenReturn(500);

        filter.doFilter(request, response, filterChain);

        verify(response, never()).setStatus(anyInt());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilter_whenURIisNull_thenProceedWithChain() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(null);
        when(request.getQueryString()).thenReturn(null);
        when(prpsConfig.getUrlMaxLength()).thenReturn(50);

        assertDoesNotThrow(() -> filter.doFilter(request, response, filterChain));
        verify(filterChain).doFilter(request, response);
}
}