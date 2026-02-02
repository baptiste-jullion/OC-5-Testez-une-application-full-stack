package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SpyBean
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
        Mockito.reset(userRepository);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private User persistUser(String email, String firstName, String lastName, boolean admin, String rawPassword) {
        return userRepository.save(User.builder()
                                       .email(email)
                                       .firstName(firstName)
                                       .lastName(lastName)
                                       .password(passwordEncoder.encode(rawPassword))
                                       .admin(admin)
                                       .build());
    }

    @Test
    @DisplayName("authenticateUser should return JWT response")
    void authenticateUser_shouldReturnJwtResponse() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail("user@example.com");
         loginRequest.setPassword("password");

         persistUser(loginRequest.getEmail(), "John", "Doe", true, loginRequest.getPassword());

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(loginRequest)))
               .andExpect(status().isOk())
             .andExpect(jsonPath("$.token").isNotEmpty())
               .andExpect(jsonPath("$.username").value(loginRequest.getEmail()))
               .andExpect(jsonPath("$.firstName").value("John"))
             .andExpect(jsonPath("$.lastName").value("Doe"))
             .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    @DisplayName("registerUser should reject duplicate email")
    void registerUser_shouldRejectDuplicateEmail() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("user@example.com");
        signupRequest.setFirstName("John");
        signupRequest.setLastName("Doe");
        signupRequest.setPassword("password");

        persistUser(signupRequest.getEmail(), signupRequest.getFirstName(), signupRequest.getLastName(), false, signupRequest.getPassword());

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(signupRequest)))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("registerUser should create new user")
    void registerUser_shouldCreateNewUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("new@example.com");
        signupRequest.setFirstName("Jane");
        signupRequest.setLastName("Smith");
        signupRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(signupRequest)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.message").value("User registered successfully!"));
        Optional<User> savedUser = userRepository.findByEmail(signupRequest.getEmail());
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getFirstName()).isEqualTo(signupRequest.getFirstName());
        assertThat(savedUser.get().getLastName()).isEqualTo(signupRequest.getLastName());
        assertThat(passwordEncoder.matches(signupRequest.getPassword(), savedUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("authenticateUser should default admin to false when user not found")
    void authenticateUser_shouldDefaultAdminToFalseWhenUserNotFound() throws Exception {
         LoginRequest loginRequest = new LoginRequest();
         loginRequest.setEmail("ghost@example.com");
         loginRequest.setPassword("password");

         User storedUser = persistUser(loginRequest.getEmail(), "Ghost", "User", true, loginRequest.getPassword());

         Mockito.doReturn(Optional.of(storedUser))
             .doReturn(Optional.empty())
             .when(userRepository)
             .findByEmail(loginRequest.getEmail());

        mockMvc.perform(post("/api/auth/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(loginRequest)))
               .andExpect(status().isOk())
             .andExpect(jsonPath("$.token").isNotEmpty())
               .andExpect(jsonPath("$.username").value(loginRequest.getEmail()))
               .andExpect(jsonPath("$.admin").value(false));
    }
}
