package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService")
public class SessionServiceTest {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    private Session session;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                   .id(1L)
                   .email("user@example.com")
                   .firstName("John")
                   .lastName("Doe")
                   .password("password")
                   .admin(false)
                   .build();

        session = Session.builder()
                         .id(10L)
                         .name("Test Session")
                         .users(new ArrayList<>())
                         .build();
    }

    @Test
    @DisplayName("Create a Session")
    public void createSession() {
        when(sessionRepository.save(session)).thenReturn(session);

        Session createdSession = sessionService.create(session);

        assertThat(createdSession).isNotNull();
        assertThat(createdSession).usingRecursiveComparison()
                                  .isEqualTo(session);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Delete a Session")
    public void deleteSession() {
        Long sessionId = 1L;

        sessionService.delete(sessionId);

        verify(sessionRepository, times(1)).deleteById(sessionId);
    }

    @Test
    @DisplayName("Find all Sessions")
    public void findAllSessions() {
        List<Session> sessions = Arrays.asList(session, new Session());
        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertThat(result).hasSize(2);
        verify(sessionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get Session by ID")
    public void getSessionById() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));

        Session foundSession = sessionService.getById(session.getId());

        assertThat(foundSession).isEqualTo(session);
    }

    @Test
    @DisplayName("Update a Session")
    public void updateSession() {
        Long newId = 20L;

        when(sessionRepository.save(session)).thenReturn(session);

        Session updatedSession = sessionService.update(newId, session);

        assertThat(updatedSession).isNotNull();
        assertThat(updatedSession).usingRecursiveComparison()
                                  .isEqualTo(session);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Participate - Success")
    public void participateSuccess() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        sessionService.participate(session.getId(), user.getId());

        assertThat(session.getUsers()).contains(user);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Participate - User not found")
    public void participateUserNotFound() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(session.getId(), user.getId()));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Participate - Session not found")
    public void participateSessionNotFound() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.empty());
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        assertThrows(NotFoundException.class, () -> sessionService.participate(session.getId(), user.getId()));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Participate - Already participating")
    public void participateAlreadyParticipating() {
        session.getUsers()
               .add(user);
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        assertThrows(BadRequestException.class, () -> sessionService.participate(session.getId(), user.getId()));
    }

    @Test
    @DisplayName("No Longer Participate - Success")
    public void noLongerParticipateSuccess() {
        session.getUsers()
               .add(user);

        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));

        sessionService.noLongerParticipate(session.getId(), user.getId());

        assertThat(session.getUsers()).doesNotContain(user);
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("No Longer Participate - Session not found")
    public void noLongerParticipateSessionNotFound() {
        Long wrongSessionId = session.getId() + 1;

        when(sessionRepository.findById(wrongSessionId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.noLongerParticipate(wrongSessionId, user.getId()));
    }

    @Test
    @DisplayName("No Longer Participate - Not participating")
    public void noLongerParticipateNotParticipating() {
        when(sessionRepository.findById(session.getId())).thenReturn(Optional.ofNullable(session));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(session.getId(), user.getId()));
    }


}

