package com.itachallenge.challenge.config;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.itachallenge.challenge.security.AuthenticationManager;
import com.itachallenge.challenge.security.SecurityContextRepository;

import lombok.AllArgsConstructor;

@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@SpringBootTest(classes = {SecurityConfig.class, AuthenticationManager.class, SecurityContextRepository.class})
@AutoConfigureWebTestClient
public class SecurityConfig_Test {

    @Autowired
    public WebTestClient webtestclient;

    @Test
    public void testProfileConfigAnyUser() {
    	//any user can access this endpoint
        webtestclient.get().uri("/itachallenge/api/v1/challenge/test")
            .exchange()
            .expectStatus().isOk();
    }
    
    @Test
    public void testGetMethodProfileConfigWithNOTAuthenticatedUser() {
    	//only authenticated users can acces get endpoints, if they arent, then they are redirected to login page
        webtestclient.get().uri("/itachallenge/api/v1/challenge/challenges")
            .exchange()
            .expectStatus().is3xxRedirection();
    }
    
    @Test
    @WithMockUser
    public void testGetMethodProfileConfigWithAuthenticatedUser() {
    	//mockuser is authenticated so it is allowed to the get endpoint
        webtestclient.get().uri("/itachallenge/api/v1/challenge/challenges")
            .exchange()
            .expectStatus().isOk();
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetMethodProfileConfigWithAuthenticatedAdmin() {
    	//mockuser is authenticated as an ADMIN so it is allowed to the get endpoint
        webtestclient.get().uri("/itachallenge/api/v1/challenge/challenges")
            .exchange()
            .expectStatus().isOk();
    }
    
    /*@Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteMethodConfigWithAuthenticatedAdminUser() {
    		//users with the admin role get to delete endpoints
             webtestclient.delete().uri("/itachallenge/api/v1/challenge/resources/301c74dd-27b6-4be4-9a38-0c7ada2969cf")
            .exchange()
            .expectStatus().isOk();
    }*/
    
    @Test
    @WithMockUser
    public void testDeleteMethodProfileConfigWithAuthenticatedUser() {
    	//users without the admin role cannot get to delete endpoints
        webtestclient.delete().uri("/itachallenge/api/v1/challenge/resources/301c74dd-27b6-4be4-9a38-0c7ada2969cf")
            .exchange()
            .expectStatus().is4xxClientError();
    }
    
}
