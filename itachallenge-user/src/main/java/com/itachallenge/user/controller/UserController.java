package com.itachallenge.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/itachallenge/api/v1/user")
public class UserController {
    //region ATTRIBUTES
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //endregion ATTRIBUTES


    //region CONSTRUCTOR

    //endregion CONSTRUCTOR


    //region ENDPOINTS
    @Operation(summary = "Testing the App")
    @GetMapping(value = "/test")
    public String test() {
        log.info("** Saludos desde el logger **");
        return "Hello from ITA User!!!";
    }

//    @Operation(summary = "Get Basic Info of Challenge")
//    @GetMapping(value = "/statistics")
//    public ResponseEntity<?> GetBasicInfoChallenge(@RequestParam("challenge")){
//        //region VARIABLES
//
//
//        //endregion VARIABLES
//
//
//        //region ACTIONS
//
//
//        //endregion ACTIONS
//
//
//        // OUT
//
//    }
    //endregion ENDPOINTS






}

