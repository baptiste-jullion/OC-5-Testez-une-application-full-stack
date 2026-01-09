package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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

    @MockBean
    private SessionService sessionService;

    @MockBean
    private SessionMapper sessionMapper;

    private Session session;
    private SessionDto sessionDto;

    @BeforeEach
    void setup() {
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();

        session = Session.builder()
                         .id(1L)
                         .name("Morning Yoga")
                         .description("Start the day on a calm note")
                         .date(date)
                         .users(new ArrayList<>())
                         .build();

        sessionDto = new SessionDto(
                session.getId(),
                session.getName(),
                date,
                1L,
                session.getDescription(),
                List.of(1L, 2L),
                now,
                now
        );
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return session")
    void findById_shouldReturnSession() throws Exception {
        when(sessionService.getById(session.getId())).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(get("/api/session/" + session.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(sessionDto.getId()))
               .andExpect(jsonPath("$.name").value(sessionDto.getName()));
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return not found")
    void findById_shouldReturnNotFound() throws Exception {
        when(sessionService.getById(session.getId())).thenReturn(null);

        mockMvc.perform(get("/api/session/" + session.getId()))
               .andExpect(status().isNotFound());

        verify(sessionMapper, never()).toDto(any(Session.class));
    }

    @Test
    @WithMockUser
    @DisplayName("findById should return bad request")
    void findById_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/session/invalid"))
               .andExpect(status().isBadRequest());

        verify(sessionService, never()).getById(anyLong());
    }

    @Test
    @WithMockUser
    @DisplayName("findAll should return sessions")
    void findAll_shouldReturnSessions() throws Exception {
        when(sessionService.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(anyList())).thenReturn(List.of(sessionDto));

        mockMvc.perform(get("/api/session"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(sessionDto.getId()))
               .andExpect(jsonPath("$[0].name").value(sessionDto.getName()));
    }

    @Test
    @WithMockUser
    @DisplayName("create should create session")
    void create_shouldCreateSession() throws Exception {
        SessionDto requestDto = new SessionDto(
                null,
                "Sunset Flow",
                new Date(),
                2L,
                "Unwind after work",
                List.of(3L),
                null,
                null
        );

        Session createdSession = Session.builder()
                                        .id(5L)
                                        .name(requestDto.getName())
                                        .description(requestDto.getDescription())
                                        .date(requestDto.getDate())
                                        .users(new ArrayList<>())
                                        .build();

        SessionDto createdDto = new SessionDto(
                createdSession.getId(),
                createdSession.getName(),
                createdSession.getDate(),
                requestDto.getTeacher_id(),
                createdSession.getDescription(),
                requestDto.getUsers(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(createdSession);
        when(sessionService.create(createdSession)).thenReturn(createdSession);
        when(sessionMapper.toDto(createdSession)).thenReturn(createdDto);

        mockMvc.perform(post("/api/session")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(createdDto.getId()))
               .andExpect(jsonPath("$.name").value(createdDto.getName()));

        verify(sessionService).create(createdSession);
    }

    @Test
    @WithMockUser
    @DisplayName("update should update session")
    void update_shouldUpdateSession() throws Exception {
        SessionDto requestDto = new SessionDto(
                session.getId(),
                session.getName(),
                session.getDate(),
                2L,
                session.getDescription(),
                List.of(4L),
                null,
                null
        );

        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.update(eq(session.getId()), eq(session))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(sessionDto);

        mockMvc.perform(put("/api/session/" + session.getId())
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(requestDto)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(sessionDto.getId()));

        verify(sessionService).update(eq(session.getId()), eq(session));
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

        verify(sessionService, never()).update(anyLong(), any(Session.class));
    }

    @Test
    @WithMockUser
    @DisplayName("delete should delete session")
    void delete_shouldDeleteSession() throws Exception {
        when(sessionService.getById(session.getId())).thenReturn(session);

        mockMvc.perform(delete("/api/session/" + session.getId()))
               .andExpect(status().isOk());

        verify(sessionService).delete(session.getId());
    }

    @Test
    @WithMockUser
    @DisplayName("delete should return not found")
    void delete_shouldReturnNotFound() throws Exception {
        when(sessionService.getById(session.getId())).thenReturn(null);

        mockMvc.perform(delete("/api/session/" + session.getId()))
               .andExpect(status().isNotFound());

        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser
    @DisplayName("delete should return bad request")
    void delete_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/invalid"))
               .andExpect(status().isBadRequest());

        verify(sessionService, never()).delete(anyLong());
    }

    @Test
    @WithMockUser
    @DisplayName("participate should add user")
    void participate_shouldAddUser() throws Exception {
        Long userId = 7L;

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + userId))
               .andExpect(status().isOk());

        verify(sessionService).participate(session.getId(), userId);
    }

    @Test
    @WithMockUser
    @DisplayName("participate should return bad request")
    void participate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/session/invalid/participate/1"))
               .andExpect(status().isBadRequest());

        verify(sessionService, never()).participate(anyLong(), anyLong());
    }

    @Test
    @WithMockUser
    @DisplayName("noLongerParticipate should remove user")
    void noLongerParticipate_shouldRemoveUser() throws Exception {
        Long userId = 4L;

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + userId))
               .andExpect(status().isOk());

        verify(sessionService).noLongerParticipate(session.getId(), userId);
    }

    @Test
    @WithMockUser
    @DisplayName("noLongerParticipate should return bad request")
    void noLongerParticipate_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(delete("/api/session/invalid/participate/1"))
               .andExpect(status().isBadRequest());

        verify(sessionService, never()).noLongerParticipate(anyLong(), anyLong());
    }
}

