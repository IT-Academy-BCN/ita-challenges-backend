package com.itachallenge.user.filter;

import com.itachallenge.user.config.PropertiesConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@PropertySource("classpath:application-test.yml")
class MaxLengthURIFilterTest {
    //region VARIABLES
    @InjectMocks
    private MaxLengthURIFilter mMaxLengthURIFilter;
    @Autowired()
    PropertiesConfig prpsConfig;

    @Mock
    private PropertiesConfig propertiesConfig;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    //endregion VARIABLES


    //region TESTS
    @Test
    void doFilter_ToLong_Test() throws IOException, ServletException {
        //region VARIABLES
        int maxLength;
        String urlEndpoint="/itachallenge/api/v1/user/statistics";
        String uriQuery;

        //endregion VARIABLES


        //region TEST INITIALIZATION
        // Get URI maxLength
        maxLength = prpsConfig.getUrlMaxLength();

        // Create query
        uriQuery = queryCreation(100);
        // Config mokito behavior
        when(propertiesConfig.getUrlMaxLength()).thenReturn(maxLength);
        when(request.getRequestURL()).thenReturn(new StringBuffer(urlEndpoint));
        when(request.getQueryString()).thenReturn(uriQuery);

        //endregion TEST INITIALIZATION


        //region TEST
        // Execute filter
        mMaxLengthURIFilter.doFilter(request, response, filterChain);
        // Check results
        verify(response).setStatus(HttpStatus.URI_TOO_LONG.value());

        //endregion TEST

    }

    @Test
    void doFilter_OK_Test() throws IOException, ServletException {
        //region VARIABLES
        int maxLength;
        String urlEndpoint="/itachallenge/api/v1/user/statistics";
        String uriQuery;

        //endregion VARIABLES


        //region INITIALIZATION
        // Get URI maxLength
        maxLength = prpsConfig.getUrlMaxLength();

        // Create query
        uriQuery = queryCreation(10);
        // Config behavior
        when(propertiesConfig.getUrlMaxLength()).thenReturn(maxLength);
        when(request.getRequestURL()).thenReturn(new StringBuffer(urlEndpoint));
        when(request.getQueryString()).thenReturn(uriQuery);

        //endregion INITIALIZATION


        //region TEST
        // Execute filter
        mMaxLengthURIFilter.doFilter(request, response, filterChain);
        // Check results
        verify(filterChain).doFilter(request, response);

        //endregion TEST

    }

    //endregion TESTS


    //region PRIVATE METHODS
    /**
     * Method to create a query string link "challenge=UUID&", repeat 'numberUUID' times.
     * @param numberUUID Number of tiems that repeat.
     * @return String with query
     */
    private String queryCreation(int numberUUID){
        String URI_TEST = String.format("challenge=%s", UUID.randomUUID());

        for (int i = 1; i < numberUUID; i++) URI_TEST += String.format("&challenge=%s", UUID.randomUUID());

        return URI_TEST;
    }

    //region PRIVATE METHODS

}