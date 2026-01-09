package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TeacherController")
public class TeacherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeacherService teacherService;

    @Test
    @WithMockUser
    @DisplayName("getTeacher should return teacher")
    public void getTeacher_shouldReturnTeacher() throws Exception {
        Long id = 1L;
        String lastName = "Delahaye";
        String firstName = "Margot";

        Teacher teacher = new Teacher().setId(id)
                                       .setLastName(lastName)
                                       .setFirstName(firstName);

        when(teacherService.findById(id)).thenReturn(teacher);

        mockMvc.perform(get("/api/teacher/" + id))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.lastName").value(lastName))
               .andExpect(jsonPath("$.firstName").value(firstName));
    }

    @Test
    @WithMockUser
    @DisplayName("getTeacher should return not found")
    public void getTeacher_shouldReturnNotFound() throws Exception {
        Long id = 999L;
        when(teacherService.findById(id)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/" + id))
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
        Long id1 = 1L;
        String lastName1 = "Delahaye";
        String firstName1 = "Margot";

        Long id2 = 2L;
        String lastName2 = "Smith";
        String firstName2 = "John";

        Teacher teacher1 = new Teacher().setId(id1)
                                        .setLastName(lastName1)
                                        .setFirstName(firstName1);

        Teacher teacher2 = new Teacher().setId(id2)
                                        .setLastName(lastName2)
                                        .setFirstName(firstName2);

        when(teacherService.findAll()).thenReturn(java.util.Arrays.asList(teacher1, teacher2));

        mockMvc.perform(get("/api/teacher"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].lastName").value(lastName1))
               .andExpect(jsonPath("$[0].firstName").value(firstName1))
               .andExpect(jsonPath("$[1].lastName").value(lastName2))
               .andExpect(jsonPath("$[1].firstName").value(firstName2));
    }
}
