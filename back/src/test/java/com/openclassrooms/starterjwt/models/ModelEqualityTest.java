package com.openclassrooms.starterjwt.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class ModelEqualityTest {

    @Test
    @DisplayName("User equals covers nulls and types")
    void userEqualsBranches() {
        User withId = new User().setId(1L);
        User sameRef = withId;
        User nullIdA = new User();
        User nullIdB = new User();

        assertThat(withId).isNotEqualTo(null);
        assertThat(withId.equals("string")).isFalse();
        assertThat(withId).isEqualTo(sameRef);
        assertThat(nullIdA).isEqualTo(nullIdB); // both ids null should compare equal
        assertThat(withId).isNotEqualTo(nullIdA);
        assertThat(withId.canEqual(new User())).isTrue();
        assertThat(withId.canEqual("string")).isFalse();
    }

    @Test
    @DisplayName("Session equals covers nulls and types")
    void sessionEqualsBranches() {
        Session withId = new Session().setId(2L);
        Session sameRef = withId;
        Session nullIdA = new Session();
        Session nullIdB = new Session();

        assertThat(withId).isNotEqualTo(null);
        assertThat(withId.equals(42)).isFalse();
        assertThat(withId).isEqualTo(sameRef);
        assertThat(nullIdA).isEqualTo(nullIdB);
        assertThat(withId).isNotEqualTo(nullIdA);
        assertThat(withId).isNotEqualTo(new Session().setId(3L));
        assertThat(withId.canEqual(new Session())).isTrue();
        assertThat(withId.canEqual("session")).isFalse();
    }

    @Test
    @DisplayName("Teacher equals covers nulls and types")
    void teacherEqualsBranches() {
        Teacher withId = new Teacher().setId(3L);
        Teacher sameRef = withId;
        Teacher nullIdA = new Teacher();
        Teacher nullIdB = new Teacher();

        assertThat(withId).isNotEqualTo(null);
        assertThat(withId.equals(new Object())).isFalse();
        assertThat(withId).isEqualTo(sameRef);
        assertThat(nullIdA).isEqualTo(nullIdB);
        assertThat(withId).isNotEqualTo(nullIdA);
        assertThat(withId).isNotEqualTo(new Teacher().setId(5L));
        assertThat(withId.canEqual(new Teacher())).isTrue();
        assertThat(withId.canEqual("teacher")).isFalse();
    }

    @Test
    @DisplayName("User constructors populate fields")
    void userConstructorsCoverFields() {
        LocalDateTime created = LocalDateTime.now();
        User required = new User("user@yoga.com", "Last", "First", "pwd", true);
        required.setCreatedAt(created).setUpdatedAt(created);

        User full = new User(10L, "full@yoga.com", "Last", "First", "pwd", false, created, created);

        assertThat(required.getEmail()).isEqualTo("user@yoga.com");
        assertThat(required.isAdmin()).isTrue();
        assertThat(full.getId()).isEqualTo(10L);
        assertThat(full.isAdmin()).isFalse();
        assertThat(full.getCreatedAt()).isEqualTo(created);
    }

    @Test
    @DisplayName("Session all-args constructor populates associations")
    void sessionConstructorCoversFields() {
        LocalDateTime created = LocalDateTime.now();
        Teacher teacher = new Teacher(5L, "Last", "First", created, created);
        User user = new User(6L, "m@yoga.com", "Last", "First", "pwd", false, created, created);
        Date date = new Date();

        Session session = new Session(20L, "Name", date, "Desc", teacher, Collections.singletonList(user), created, created);

        assertThat(session.getId()).isEqualTo(20L);
        assertThat(session.getTeacher()).isEqualTo(teacher);
        assertThat(session.getUsers()).containsExactly(user);
        assertThat(session.getDate()).isEqualTo(date);
    }

    @Test
    @DisplayName("Teacher all-args constructor populates fields")
    void teacherConstructorCoversFields() {
        LocalDateTime created = LocalDateTime.now();
        Teacher teacher = new Teacher(30L, "Last", "First", created, created);

        assertThat(teacher.getId()).isEqualTo(30L);
        assertThat(teacher.getLastName()).isEqualTo("Last");
        assertThat(teacher.getFirstName()).isEqualTo("First");
        assertThat(teacher.getUpdatedAt()).isEqualTo(created);
    }

    @Test
    @DisplayName("User constructor and setters reject nulls")
    void userNullGuards() {
        assertThatThrownBy(() -> new User(null, "Last", "First", "pwd", true)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new User("email@yoga.com", null, "First", "pwd", false)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new User("email@yoga.com", "Last", null, "pwd", false)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new User("email@yoga.com", "Last", "First", null, false)).isInstanceOf(NullPointerException.class);

        User user = new User();
        assertThatThrownBy(() -> user.setEmail(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> user.setFirstName(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> user.setLastName(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> user.setPassword(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("User hashCode covers null and value")
    void userHashCodeBranches() {
        int nullHash = new User().hashCode();
        int withIdHash = new User().setId(50L).hashCode();

        assertThat(withIdHash).isNotEqualTo(nullHash);
        assertThat(withIdHash).isEqualTo(new User().setId(50L).hashCode());
    }

    @Test
    @DisplayName("Session hashCode covers null and value")
    void sessionHashCodeBranches() {
        Session nullSession = new Session();
        int nullHash = nullSession.hashCode();

        Session withId = new Session().setId(15L);
        assertThat(withId.hashCode()).isNotEqualTo(nullHash);
        assertThat(withId.hashCode()).isEqualTo(new Session().setId(15L).hashCode());
    }

    @Test
    @DisplayName("Teacher hashCode covers null and value")
    void teacherHashCodeBranches() {
        Teacher nullTeacher = new Teacher();
        int nullHash = nullTeacher.hashCode();

        Teacher withId = new Teacher().setId(25L);
        assertThat(withId.hashCode()).isNotEqualTo(nullHash);
        assertThat(withId.hashCode()).isEqualTo(new Teacher().setId(25L).hashCode());
    }
}
