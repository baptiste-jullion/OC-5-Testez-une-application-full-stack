package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class ModelAccessorsTest {

    @Test
    @DisplayName("User setters chain and equality by id")
    void userSettersAndEquality() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = created.plusMinutes(5);

        User user = new User()
                .setId(3L)
                .setEmail("member@yoga.com")
                .setFirstName("Member")
                .setLastName("User")
                .setPassword("pwd")
                .setAdmin(false)
                .setCreatedAt(created)
                .setUpdatedAt(updated);

        User sameId = new User()
                .setId(3L)
                .setEmail("other@yoga.com")
                .setFirstName("Other")
                .setLastName("Person")
                .setPassword("pwd2")
                .setAdmin(true)
                .setCreatedAt(created)
                .setUpdatedAt(updated);

        User differentId = new User().setId(4L);

        assertThat(user).isEqualTo(sameId);
        assertThat(user).isNotEqualTo(differentId);
        assertThat(user.getEmail()).isEqualTo("member@yoga.com");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.toString()).contains("Member", "User");
    }

    @Test
    @DisplayName("Session setters cover relations and equality by id")
    void sessionSettersCoverRelations() {
        Teacher teacher = new Teacher().setId(9L).setFirstName("Taylor").setLastName("Ray");
        User attendee = new User().setId(11L).setEmail("att@yoga.com").setFirstName("Att").setLastName("Endee").setPassword("pwd").setAdmin(false);
        Date date = new Date();
        LocalDateTime created = LocalDateTime.now();

        Session session = new Session()
                .setId(20L)
                .setName("Evening Stretch")
                .setDate(date)
                .setDescription("Stretch together")
                .setTeacher(teacher)
                .setUsers(Arrays.asList(attendee))
                .setCreatedAt(created)
                .setUpdatedAt(created.plusHours(2));

        Session sameId = new Session().setId(20L);

        assertThat(session).isEqualTo(sameId);
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(attendee);
        assertThat(session.getDescription()).contains("Stretch");
        assertThat(session.getUpdatedAt()).isAfter(session.getCreatedAt());
        assertThat(session.toString()).contains("Evening Stretch");
    }

    @Test
    @DisplayName("Teacher setters populate fields and equality by id")
    void teacherSettersPopulateFields() {
        LocalDateTime created = LocalDateTime.now();

        Teacher teacher = new Teacher()
                .setId(7L)
                .setFirstName("Alex")
                .setLastName("Morgan")
                .setCreatedAt(created)
                .setUpdatedAt(created.plusDays(1));

        Teacher sameId = new Teacher().setId(7L);
        Teacher differentId = new Teacher().setId(8L);

        assertThat(teacher).isEqualTo(sameId);
        assertThat(teacher).isNotEqualTo(differentId);
        assertThat(teacher.getFirstName()).isEqualTo("Alex");
        assertThat(teacher.getLastName()).isEqualTo("Morgan");
        assertThat(teacher.getUpdatedAt()).isAfter(teacher.getCreatedAt());
    }
}
