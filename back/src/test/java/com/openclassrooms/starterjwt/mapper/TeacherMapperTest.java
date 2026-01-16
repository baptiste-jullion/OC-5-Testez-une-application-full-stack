package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TeacherMapperTest {

    private final TeacherMapper mapper = Mappers.getMapper(TeacherMapper.class);

    @Test
    @DisplayName("toDto mirrors entity content")
    void toDto_shouldCopyFields() {
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder()
                .id(5L)
                .firstName("Jane")
                .lastName("Doe")
                .createdAt(now)
                .updatedAt(now)
                .build();

        TeacherDto dto = mapper.toDto(teacher);

        assertThat(dto).usingRecursiveComparison().isEqualTo(new TeacherDto(5L, "Doe", "Jane", now, now));
    }

    @Test
    @DisplayName("toEntity mirrors dto content")
    void toEntity_shouldCopyFields() {
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto = new TeacherDto(8L, "Smith", "John", now, now);

        Teacher teacher = mapper.toEntity(dto);

        assertThat(teacher.getId()).isEqualTo(8L);
        assertThat(teacher.getLastName()).isEqualTo("Smith");
        assertThat(teacher.getFirstName()).isEqualTo("John");
        assertThat(teacher.getCreatedAt()).isEqualTo(now);
        assertThat(teacher.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("list helpers cover collection mappings")
    void listHelpers_shouldMapCollections() {
        Teacher teacher = Teacher.builder().id(1L).firstName("A").lastName("B").build();
        TeacherDto dto = new TeacherDto(2L, "Doe", "Jane", null, null);

        assertThat(mapper.toDto((Teacher) null)).isNull();
        assertThat(mapper.toEntity((TeacherDto) null)).isNull();
        assertThat(mapper.toDto((java.util.List<Teacher>) null)).isNull();
        assertThat(mapper.toEntity((java.util.List<TeacherDto>) null)).isNull();

        assertThat(mapper.toDto(java.util.List.of(teacher))).hasSize(1);
        assertThat(mapper.toEntity(java.util.List.of(dto))).hasSize(1);
    }
}
