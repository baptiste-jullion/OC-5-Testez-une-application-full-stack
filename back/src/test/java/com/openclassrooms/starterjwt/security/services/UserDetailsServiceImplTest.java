package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("loadUserByUsername returns mapped user details")
    void loadUserByUsername_shouldReturnUserDetails() {
        User user = User.builder()
                .id(3L)
                .email("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("pwd")
                .admin(true)
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("user@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("user@example.com");
        assertThat(userDetails.getPassword()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("loadUserByUsername throws when user missing")
    void loadUserByUsername_shouldThrowWhenMissing() {
        when(userRepository.findByEmail("absent@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("absent@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("absent@example.com");
    }
}
