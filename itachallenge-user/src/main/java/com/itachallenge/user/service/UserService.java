package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    UserRepository userRepository;

    public Mono<ResponseEntity<String>> isMentor(Mono<String> username){
        return username.flatMap(user -> userRepository.findUsername(user))
                .map(existingUsername -> ResponseEntity.status(HttpStatus.OK).body(existingUsername))
                .switchIfEmpty((Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username is not related to a mentor. "))));

    }
}
