package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TeacherController")
public class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    private Teacher teacherOne;
    private Teacher teacherTwo;

    @BeforeEach
    void setup() {
        teacherRepository.deleteAll();
        teacherOne = teacherRepository.save(Teacher.builder()
                                                   .lastName("Delahaye")
                                                   .firstName("Margot")
                                                   .build());
        teacherTwo = teacherRepository.save(Teacher.builder()
                                                   .lastName("Smith")
                                                   .firstName("John")
                                                   .build());
    }

    @AfterEach
    void tearDown() {
        teacherRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("getTeacher should return teacher")
    public void getTeacher_shouldReturnTeacher() throws Exception {
        mockMvc.perform(get("/api/teacher/" + teacherOne.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.lastName").value(teacherOne.getLastName()))
               .andExpect(jsonPath("$.firstName").value(teacherOne.getFirstName()));
    }

    @Test
    @WithMockUser
    @DisplayName("getTeacher should return not found")
    public void getTeacher_shouldReturnNotFound() throws Exception {
        long missingId = teacherTwo.getId() + 100;

        mockMvc.perform(get("/api/teacher/" + missingId))
               .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    @DisplayName("getTeacher should return bad request")
    public void getTeacher_shouldReturnBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(get("/api/teacher/" + invalidId))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("findAllTeachers should return list of teachers")
    public void findAllTeachers_shouldReturnListOfTeachers() throws Exception {
        mockMvc.perform(get("/api/teacher"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].lastName").value(teacherOne.getLastName()))
               .andExpect(jsonPath("$[0].firstName").value(teacherOne.getFirstName()))
               .andExpect(jsonPath("$[1].lastName").value(teacherTwo.getLastName()))
               .andExpect(jsonPath("$[1].firstName").value(teacherTwo.getFirstName()));
    }
}
