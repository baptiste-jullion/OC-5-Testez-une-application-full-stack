package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                   .id(1L)
                   .firstName("John")
                   .lastName("Doe")
                   .email("user@example.com")
                   .password("password")
                   .admin(false)
                   .build();
    }

    @Test
    @DisplayName("findById should return user by id")
    void findById_shouldReturnUserById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User foundUser = userService.findById(user.getId());

        assertThat(foundUser).usingRecursiveComparison()
                             .isEqualTo(user);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("delete should delete user by id")
    void delete_shouldDeleteUserById() {
        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }
}
