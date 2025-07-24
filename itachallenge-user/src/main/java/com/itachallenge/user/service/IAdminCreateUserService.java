package com.itachallenge.user.service;

import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import reactor.core.publisher.Mono;

public interface IAdminCreateUserService {

    Mono<AdminCreateUserResponseDto> createUser(AdminCreateUserRequestDto request);

}
