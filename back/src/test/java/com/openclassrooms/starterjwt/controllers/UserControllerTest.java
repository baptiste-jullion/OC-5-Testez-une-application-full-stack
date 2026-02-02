package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        SecurityContextHolder.clearContext();
    }

    private User persistUser() {
        return userRepository.save(User.builder()
                                       .email("user@example.com")
                                       .firstName("John")
                                       .lastName("Doe")
                                       .password(passwordEncoder.encode("password"))
                                       .admin(false)
                                       .build());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("findById should return user")
    void findById_shouldReturnUser() throws Exception {
        User savedUser = persistUser();

        mockMvc.perform(get("/api/user/" + savedUser.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(savedUser.getId()))
               .andExpect(jsonPath("$.email").value(savedUser.getEmail()))
               .andExpect(jsonPath("$.firstName").value(savedUser.getFirstName()))
               .andExpect(jsonPath("$.lastName").value(savedUser.getLastName()));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("findById should return not found")
    void findById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user/999"))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("findById should return bad request")
    void findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/user/invalid"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("delete should remove user")
    void delete_shouldRemoveUser() throws Exception {
        User savedUser = persistUser();

        mockMvc.perform(delete("/api/user/" + savedUser.getId()))
               .andExpect(status().isOk());

        assertThat(userRepository.existsById(savedUser.getId())).isFalse();
    }

    @Test
    @WithMockUser(username = "other@example.com")
    @DisplayName("delete should return unauthorized when different user")
    void delete_shouldReturnUnauthorizedWhenDifferentUser() throws Exception {
        User savedUser = persistUser();

        mockMvc.perform(delete("/api/user/" + savedUser.getId()))
               .andExpect(status().isUnauthorized());

        assertThat(userRepository.existsById(savedUser.getId())).isTrue();
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("delete should return not found")
    void delete_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/user/123"))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("delete should return bad request")
    void delete_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/user/invalid"))
               .andExpect(status().isBadRequest());
    }
}
