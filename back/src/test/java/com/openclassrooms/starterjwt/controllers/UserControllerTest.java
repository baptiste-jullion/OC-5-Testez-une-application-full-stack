package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
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

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = User.builder()
                   .id(1L)
                   .firstName("John")
                   .lastName("Doe")
                   .email("user@example.com")
                   .admin(false)
                   .password("password")
                   .createdAt(LocalDateTime.now())
                   .updatedAt(LocalDateTime.now())
                   .build();

        userDto = new UserDto(
                user.getId(),
                user.getEmail(),
                user.getLastName(),
                user.getFirstName(),
                user.isAdmin(),
                null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Get user by ID - success")
    void findById_shouldReturnUser() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        mockMvc.perform(get("/api/user/" + user.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(userDto.getId()))
               .andExpect(jsonPath("$.email").value(userDto.getEmail()))
               .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
               .andExpect(jsonPath("$.lastName").value(userDto.getLastName()));

        verify(userService).findById(user.getId());
        verify(userMapper).toDto(user);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Get user by ID - not found")
    void findById_shouldReturnNotFound() throws Exception {
        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(get("/api/user/" + user.getId()))
               .andExpect(status().isNotFound());

        verify(userService).findById(user.getId());
        verifyNoInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Get user by ID - bad request")
    void findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/user/invalid"))
               .andExpect(status().isBadRequest());

        verifyNoInteractions(userService, userMapper);
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Delete user by ID - success")
    void delete_shouldRemoveUser() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(delete("/api/user/" + user.getId()))
               .andExpect(status().isOk());

        verify(userService).findById(user.getId());
        verify(userService).delete(user.getId());
    }

    @Test
    @WithMockUser(username = "other@example.com")
    @DisplayName("Delete user by ID - unauthorized")
    void delete_shouldReturnUnauthorizedWhenDifferentUser() throws Exception {
        when(userService.findById(user.getId())).thenReturn(user);

        mockMvc.perform(delete("/api/user/" + user.getId()))
               .andExpect(status().isUnauthorized());

        verify(userService).findById(user.getId());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Delete user by ID - not found")
    void delete_shouldReturnNotFound() throws Exception {
        when(userService.findById(user.getId())).thenReturn(null);

        mockMvc.perform(delete("/api/user/" + user.getId()))
               .andExpect(status().isNotFound());

        verify(userService).findById(user.getId());
        verify(userService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    @DisplayName("Delete user by ID - bad request")
    void delete_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/user/invalid"))
               .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}

