package com.itachallenge.user.service;

import com.itachallenge.user.document.UserDocument;
import com.itachallenge.user.dto.AdminCreateUserRequestDto;
import com.itachallenge.user.dto.AdminCreateUserResponseDto;
import com.itachallenge.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminCreateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminCreateUserService adminCreateUserService;

    @Test
    @DisplayName("Test: Create users with a mixed list of new and existing usernames")
    void createUsers_withMixedList_shouldReturnCorrectResponse() {
        AdminCreateUserRequestDto request = new AdminCreateUserRequestDto();
        request.setUsernames(List.of("newUser1", "existingUser", "newUser2"));

        UserDocument existingUser = UserDocument.builder().username("existingUser").build();

        when(userRepository.findByUsername("newUser1")).thenReturn(Mono.empty());
        when(userRepository.findByUsername("newUser2")).thenReturn(Mono.empty());
        when(userRepository.save(any(UserDocument.class))).thenAnswer(invocation ->
                Mono.just(invocation.getArgument(0))
        );

        when(userRepository.findByUsername("existingUser")).thenReturn(Mono.just(existingUser));

        Mono<AdminCreateUserResponseDto> result = adminCreateUserService.createUsers(request);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getCreatedUsers()).hasSize(2);
                    assertThat(response.getCreatedUsers())
                            .extracting(AdminCreateUserResponseDto.UserCreatedDto::getUsername)
                            .containsExactlyInAnyOrder("newUser1", "newUser2");

                    assertThat(response.getExistingUsers()).hasSize(1);
                    assertThat(response.getExistingUsers().get(0)).isEqualTo("existingUser");
                })
                .verifyComplete();

        verify(userRepository, times(2)).save(any(UserDocument.class));
    }
}