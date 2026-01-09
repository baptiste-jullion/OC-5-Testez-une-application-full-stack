package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService")
public class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    private Teacher teacher;

    @BeforeEach
    public void setup() {
        teacher = Teacher.builder()
                         .id(1L)
                         .firstName("Jane")
                         .lastName("Doe")
                         .build();
    }

    @Test
    @DisplayName("findAll should return list of teachers")
    public void findAll_shouldReturnListOfTeachers() {
        List<Teacher> teachers = Arrays.asList(teacher, Teacher.builder()
                                                               .id(2L)
                                                               .build());
        when(teacherRepository.findAll()).thenReturn(teachers);

        List<Teacher> result = teacherService.findAll();

        assertThat(result).isEqualTo(teachers);
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById should return teacher by id")
    public void findById_shouldReturnTeacherById() {
        when(teacherRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(teacher.getId());

        assertThat(result).isEqualTo(teacher);
        verify(teacherRepository, times(1)).findById(teacher.getId());
    }

    @Test
    @DisplayName("findById should return null when teacher does not exist")
    public void findById_shouldReturnNullWhenTeacherDoesNotExist() {
        Long unknownId = 999L;
        when(teacherRepository.findById(unknownId)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(unknownId);

        assertThat(result).isNull();
        verify(teacherRepository, times(1)).findById(unknownId);
    }
}
