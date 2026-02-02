package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SessionController")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User attendeeOne;
    private User attendeeTwo;
    private Session session;

    @BeforeEach
    void setup() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        teacher = teacherRepository.save(Teacher.builder()
                                               .firstName("Margot")
                                               .lastName("Delahaye")
                                               .build());

        attendeeOne = createUser("attendee1@example.com", "Alice", "Flow");
        attendeeTwo = createUser("attendee2@example.com", "Bob", "Zen");

        session = sessionRepository.save(Session.builder()
                                               .name("Morning Yoga")
                                               .description("Start the day on a calm note")
                                               .date(new Date())
                                               .teacher(teacher)
                                               .users(new ArrayList<>())
                                               .build());
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
    }

    private User createUser(String email, String firstName, String lastName) {
        return userRepository.save(User.builder()
                                       .email(email)
                                       .firstName(firstName)
                                       .lastName(lastName)
                                       .password("password")
                                       .admin(false)
                                       .build());
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return session")
    void findById_shouldReturnSession() throws Exception {
        mockMvc.perform(get("/api/session/" + session.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(session.getId()))
               .andExpect(jsonPath("$.name").value(session.getName()))
               .andExpect(jsonPath("$.teacher_id").value(teacher.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return not found")
    void findById_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/session/" + (session.getId() + 99)))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return bad request")
    void findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/session/invalid"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("findAll should return sessions")
    void findAll_shouldReturnSessions() throws Exception {
        Session other = sessionRepository.save(Session.builder()
                                                      .name("Evening Stretch")
                                                      .description("Recover after work")
                                                      .date(new Date())
                                                      .teacher(teacher)
                                                      .users(new ArrayList<>())
                                                      .build());

        mockMvc.perform(get("/api/session"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(2)))
               .andExpect(jsonPath("$[0].id").value(session.getId()))
               .andExpect(jsonPath("$[1].id").value(other.getId()));
    }

    @Test
    @WithMockUser
    @DisplayName("create should create session")
    void create_shouldCreateSession() throws Exception {
        SessionDto requestDto = new SessionDto(
            null,
            "Sunset Flow",
            new Date(),
            teacher.getId(),
            "Unwind after work",
            List.of(attendeeOne.getId(), attendeeTwo.getId()),
            null,
            null
        );

        mockMvc.perform(post("/api/session")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Sunset Flow"))
               .andExpect(jsonPath("$.users", hasSize(2)))
               .andExpect(jsonPath("$.users[0]").value(attendeeOne.getId()));

        assertThat(sessionRepository.count()).isEqualTo(2);
    }

    @Test
    @WithMockUser
    @DisplayName("update should update session")
    void update_shouldUpdateSession() throws Exception {
        SessionDto requestDto = new SessionDto(
                session.getId(),
                "Updated Session",
                session.getDate(),
                teacher.getId(),
                "Updated description",
                List.of(attendeeTwo.getId()),
                null,
                null
        );

        mockMvc.perform(put("/api/session/" + session.getId())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Updated Session"))
               .andExpect(jsonPath("$.description").value("Updated description"))
               .andExpect(jsonPath("$.users[0]").value(attendeeTwo.getId()));

        Session updated = sessionRepository.findById(session.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Session");
    }

    @Test
    @WithMockUser
    @DisplayName("update should return bad request when id invalid")
    void update_shouldReturnBadRequest() throws Exception {
        SessionDto requestDto = new SessionDto(
                null,
                "Any",
                new Date(),
                1L,
                "desc",
                List.of(1L),
                null,
                null
        );

        mockMvc.perform(put("/api/session/invalid")
                   .contentType(MediaType.APPLICATION_JSON)
                   .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("delete should delete session")
    void delete_shouldDeleteSession() throws Exception {
        mockMvc.perform(delete("/api/session/" + session.getId()))
               .andExpect(status().isOk());

        assertThat(sessionRepository.existsById(session.getId())).isFalse();
    }

    @Test
    @WithMockUser
    @DisplayName("delete should return not found")
    void delete_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/session/" + (session.getId() + 55)))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("delete should return bad request")
    void delete_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/invalid"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("participate should add user")
    void participate_shouldAddUser() throws Exception {
        Long userId = attendeeOne.getId();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + userId))
               .andExpect(status().isOk());

        Session updated = sessionRepository.findById(session.getId()).orElseThrow();
        assertThat(updated.getUsers()).extracting(User::getId).containsExactly(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("participate should return bad request")
    void participate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/invalid/participate/1"))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("noLongerParticipate should remove user")
    void noLongerParticipate_shouldRemoveUser() throws Exception {
        session.getUsers().add(attendeeTwo);
        sessionRepository.save(session);
        Long userId = attendeeTwo.getId();

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + userId))
               .andExpect(status().isOk());

        Session updated = sessionRepository.findById(session.getId()).orElseThrow();
        assertThat(updated.getUsers()).isEmpty();
    }

    @Test
    @WithMockUser
    @DisplayName("noLongerParticipate should return bad request")
    void noLongerParticipate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/invalid/participate/1"))
               .andExpect(status().isBadRequest());
    }
}

