package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("authenticateUser should return JWT response")
    void authenticateUser_shouldReturnJwtResponse() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                                                     .id(1L)
                                                     .username(loginRequest.getEmail())
                                                     .firstName("John")
                                                     .lastName("Doe")
                                                     .admin(false)
                                                     .password("encoded")
                                                     .build();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

        User storedUser = User.builder()
                              .id(1L)
                              .email(loginRequest.getEmail())
                              .firstName("John")
                              .lastName("Doe")
                              .password("encoded")
                              .admin(true)
                              .build();
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(storedUser));

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(loginRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value("jwt-token"))
               .andExpect(jsonPath("$.username").value(loginRequest.getEmail()))
               .andExpect(jsonPath("$.firstName").value("John"))
               .andExpect(jsonPath("$.admin").value(true));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("registerUser should reject duplicate email")
    void registerUser_shouldRejectDuplicateEmail() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(signupRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("registerUser should create new user")
    void registerUser_shouldCreateNewUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("hashed-password");

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(signupRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("User registered successfully!"));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo(signupRequest.getEmail());
        assertThat(savedUser.getFirstName()).isEqualTo(signupRequest.getFirstName());
        assertThat(savedUser.getLastName()).isEqualTo(signupRequest.getLastName());
        assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
    }
}

