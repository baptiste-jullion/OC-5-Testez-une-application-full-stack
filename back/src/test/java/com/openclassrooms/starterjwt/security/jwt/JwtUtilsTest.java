package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "testSecretKeyForJwtUtils1234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    @Test
    @DisplayName("generateJwtToken produces a valid token")
    void generateJwtToken_shouldReturnSignedToken() {
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .password("pwd")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("validateJwtToken returns false for malformed tokens")
    void validateJwtToken_shouldReturnFalseForInvalid() {
        boolean result = jwtUtils.validateJwtToken("invalid.token.value");

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken returns false when signature differs")
    void validateJwtToken_shouldReturnFalseForBadSignature() {
        JwtUtils otherSigner = new JwtUtils();
        ReflectionTestUtils.setField(otherSigner, "jwtSecret", "anotherSecretKey12345678901");
        ReflectionTestUtils.setField(otherSigner, "jwtExpirationMs", 3600000);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(3L)
                .username("signed@example.com")
                .password("pwd")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = otherSigner.generateJwtToken(authentication);

        assertThat(jwtUtils.validateJwtToken(token)).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken returns false when token blank")
    void validateJwtToken_shouldReturnFalseForBlankInput() {
        assertThat(jwtUtils.validateJwtToken(" ")).isFalse();
    }

    @Test
    @DisplayName("validateJwtToken returns false when token is expired")
    void validateJwtToken_shouldReturnFalseWhenExpired() {
        JwtUtils expiredJwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtSecret", "testSecretKeyForJwtUtils1234567890");
        ReflectionTestUtils.setField(expiredJwtUtils, "jwtExpirationMs", -1000);
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(2L)
                .username("expired@example.com")
                .password("pwd")
                .build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        String token = expiredJwtUtils.generateJwtToken(authentication);

        assertThat(expiredJwtUtils.validateJwtToken(token)).isFalse();
    }
}
