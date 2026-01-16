package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionMapperTest {

    @Mock
    private TeacherService teacherService;

    @Mock
    private UserService userService;

    private SessionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(SessionMapper.class);
        ReflectionTestUtils.setField(mapper, "teacherService", teacherService);
        ReflectionTestUtils.setField(mapper, "userService", userService);
    }

    @Test
    @DisplayName("toEntity maps dto to entity with teacher and users")
    void toEntity_shouldMapAllFields() {
        Date date = new Date();
        SessionDto dto = new SessionDto(1L, "Morning flow", date, 42L, "Relax", Arrays.asList(10L, 11L), null, null);
        Teacher teacher = Teacher.builder().id(42L).firstName("John").lastName("Doe").build();
        User userA = User.builder().id(10L).email("a@example.com").firstName("A").lastName("User").password("pwd").admin(false).build();
        User userB = User.builder().id(11L).email("b@example.com").firstName("B").lastName("User").password("pwd").admin(false).build();

        when(teacherService.findById(42L)).thenReturn(teacher);
        when(userService.findById(10L)).thenReturn(userA);
        when(userService.findById(11L)).thenReturn(userB);

        Session entity = mapper.toEntity(dto);

        assertThat(entity.getName()).isEqualTo("Morning flow");
        assertThat(entity.getDate()).isEqualTo(date);
        assertThat(entity.getDescription()).isEqualTo("Relax");
        assertThat(entity.getTeacher()).isEqualTo(teacher);
        assertThat(entity.getUsers()).containsExactly(userA, userB);
    }

    @Test
    @DisplayName("toDto maps entity to dto with teacher and user ids")
    void toDto_shouldMapAllFields() {
        Date date = new Date();
        Teacher teacher = Teacher.builder().id(7L).firstName("Jane").lastName("Doe").build();
        User userA = User.builder().id(1L).email("a@example.com").firstName("A").lastName("User").password("pwd").admin(false).build();
        User userB = User.builder().id(2L).email("b@example.com").firstName("B").lastName("User").password("pwd").admin(false).build();
        Session session = Session.builder()
                .id(9L)
                .name("Focus")
                .date(date)
                .description("Desc")
                .teacher(teacher)
                .users(List.of(userA, userB))
                .build();

        SessionDto dto = mapper.toDto(session);

        assertThat(dto.getId()).isEqualTo(9L);
        assertThat(dto.getName()).isEqualTo("Focus");
        assertThat(dto.getDate()).isEqualTo(date);
        assertThat(dto.getDescription()).isEqualTo("Desc");
        assertThat(dto.getTeacher_id()).isEqualTo(7L);
        assertThat(dto.getUsers()).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("toEntity handles null collections and teacher")
    void toEntity_shouldHandleNullCollections() {
        SessionDto dto = new SessionDto();
        dto.setName("Solo");
        dto.setDescription("Desc");
        dto.setDate(new Date());

        Session entity = mapper.toEntity(dto);

        assertThat(entity.getTeacher()).isNull();
        assertThat(entity.getUsers()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("list helpers delegate to the single mapping methods")
    void listHelpers_shouldMapCollections() {
        Date date = new Date();
        SessionDto dto = new SessionDto(1L, "Morning flow", date, null, "Relax", null, null, null);
        Session session = Session.builder()
                .id(9L)
                .name("Focus")
                .date(date)
                .description("Desc")
                .users(List.of())
                .build();

        List<Session> entities = mapper.toEntity(List.of(dto));
        List<SessionDto> dtos = mapper.toDto(List.of(session));

        assertThat(entities).hasSize(1);
        assertThat(dtos).hasSize(1);
    }

    @Test
    @DisplayName("null inputs short circuit MapStruct helpers")
    void nullInputs_shouldReturnNull() {
        assertThat(mapper.toEntity((SessionDto) null)).isNull();
        assertThat(mapper.toDto((Session) null)).isNull();
        assertThat(mapper.toEntity((List<SessionDto>) null)).isNull();
        assertThat(mapper.toDto((List<Session>) null)).isNull();
    }
}
