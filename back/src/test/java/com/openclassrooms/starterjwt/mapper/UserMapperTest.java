package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toDto mirrors user entity")
    void toDto_shouldCopyFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .admin(true)
                .password("secret")
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserDto dto = mapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getFirstName()).isEqualTo("Jane");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.isAdmin()).isTrue();
        assertThat(dto.getPassword()).isEqualTo("secret");
    }

    @Test
    @DisplayName("toEntity mirrors user dto")
    void toEntity_shouldCopyFields() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto(2L, "user2@example.com", "Smith", "John", false, "pwd", now, now);

        User user = mapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getEmail()).isEqualTo("user2@example.com");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Smith");
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.getPassword()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("list helpers cover collection mappings")
    void listHelpers_shouldMapCollections() {
        User user = User.builder().id(1L).email("user@example.com").firstName("Jane").lastName("Doe").password("pwd").admin(false).build();
        UserDto dto = new UserDto(2L, "user2@example.com", "Smith", "John", true, "pwd", null, null);

        assertThat(mapper.toDto((User) null)).isNull();
        assertThat(mapper.toEntity((UserDto) null)).isNull();
        assertThat(mapper.toDto((java.util.List<User>) null)).isNull();
        assertThat(mapper.toEntity((java.util.List<UserDto>) null)).isNull();

        assertThat(mapper.toDto(java.util.List.of(user))).hasSize(1);
        assertThat(mapper.toEntity(java.util.List.of(dto))).hasSize(1);
    }
}
