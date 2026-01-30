package com.openclassrooms.starterjwt.security.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDetailsImplTest {

    @Test
    @DisplayName("UserDetailsImpl exposes fixed security flags and authorities")
    void shouldExposeDefaultSecurityFlags() {
        UserDetailsImpl details = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .password("pwd")
                .admin(false)
                .build();

        assertThat(details.getAuthorities()).isEmpty();
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAdmin()).isFalse();
    }

    @Test
    @DisplayName("equals compares only identifiers")
    void equalsShouldRelyOnId() {
        UserDetailsImpl left = UserDetailsImpl.builder().id(1L).username("user@example.com").build();
        UserDetailsImpl right = UserDetailsImpl.builder().id(1L).username("other@example.com").build();
        UserDetailsImpl other = UserDetailsImpl.builder().id(2L).username("user@example.com").build();

        assertThat(left).isEqualTo(right);
        assertThat(left).isNotEqualTo(other);
    }

    @Test
    @DisplayName("equals handles null, different class and same instance")
    void equalsEdgeCases() {
        UserDetailsImpl details = UserDetailsImpl.builder().id(1L).username("user@example.com").build();

        assertThat(details.equals(null)).isFalse();

        assertThat(details.equals("a string")).isFalse();

        assertThat(details.equals(details)).isTrue();
    }

    @Test
    @DisplayName("Builder toString is callable and non-null")
    void builderToStringIsCallable() {
        UserDetailsImpl.UserDetailsImplBuilder builder = UserDetailsImpl.builder()
                .id(10L)
                .username("builder@example.com")
                .admin(true);

        String s = builder.toString();
        assertThat(s).isNotNull();
    }

    @Test
    @DisplayName("getAdmin returns true when set")
    void getAdminShouldReturnValue() {
        UserDetailsImpl details = UserDetailsImpl.builder()
                .id(2L)
                .username("admin@example.com")
                .admin(true)
                .build();

        assertThat(details.getAdmin()).isTrue();
    }
}
