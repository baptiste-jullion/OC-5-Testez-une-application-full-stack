package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ModelTest {

    @Test
    @DisplayName("User builder and equality work as expected")
    void userBuilderAndEquality() {
        LocalDateTime now = LocalDateTime.now();

        User adminUser = User.builder()
                .id(1L)
                .email("admin@yoga.com")
                .firstName("Admin")
                .lastName("User")
                .password("secret")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User sameIdDifferentNames = User.builder()
                .id(1L)
                .email("other@yoga.com")
                .firstName("Other")
                .lastName("Person")
                .password("secret")
                .admin(false)
                .createdAt(now)
                .updatedAt(now)
                .build();

        User differentId = User.builder()
                .id(2L)
                .email("admin@yoga.com")
                .firstName("Admin")
                .lastName("User")
                .password("secret")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertThat(adminUser.getEmail()).isEqualTo("admin@yoga.com");
        assertThat(adminUser.isAdmin()).isTrue();
        assertThat(adminUser.toString()).contains("Admin", "User");
        assertThat(adminUser).isEqualTo(sameIdDifferentNames);
        assertThat(adminUser).isNotEqualTo(differentId);
        assertThat(adminUser.hashCode()).isEqualTo(sameIdDifferentNames.hashCode());
    }

    @Test
    @DisplayName("Teacher builder populates fields")
    void teacherBuilderPopulatesFields() {
        LocalDateTime created = LocalDateTime.now();

        Teacher teacher = Teacher.builder()
                .id(5L)
                .firstName("Jane")
                .lastName("Doe")
                .createdAt(created)
                .updatedAt(created.plusDays(1))
                .build();

        assertThat(teacher.getId()).isEqualTo(5L);
        assertThat(teacher.getFirstName()).isEqualTo("Jane");
        assertThat(teacher.getLastName()).isEqualTo("Doe");
        assertThat(teacher.getCreatedAt()).isEqualTo(created);
        assertThat(teacher.getUpdatedAt()).isAfter(created);
    }

    @Test
    @DisplayName("Session builder handles relations and dates")
    void sessionBuilderHandlesRelations() {
        LocalDateTime created = LocalDateTime.now();
        Teacher teacher = Teacher.builder().id(8L).firstName("Alex").lastName("Smith").build();
        User attendee = User.builder().id(42L).email("attendee@yoga.com").firstName("Att").lastName("Endee").password("pwd").admin(false).build();

        Session session = Session.builder()
                .id(10L)
                .name("Morning Flow")
                .date(new Date())
                .description("Relaxing flow session")
                .teacher(teacher)
                .users(Collections.singletonList(attendee))
                .createdAt(created)
                .updatedAt(created.plusHours(1))
                .build();

        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(attendee);
        assertThat(session.getCreatedAt()).isEqualTo(created);
        assertThat(session.getUpdatedAt()).isAfterOrEqualTo(created);
        assertThat(session.getName()).isEqualTo("Morning Flow");
    }
}
