package com.openclassrooms.starterjwt.payload.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayloadRequestTest {

    @Test
    @DisplayName("LoginRequest getters and setters work")
    void loginRequestAccessors() {
        LoginRequest request = new LoginRequest();
        request.setEmail("login@yoga.com");
        request.setPassword("strongPass");

        assertThat(request.getEmail()).isEqualTo("login@yoga.com");
        assertThat(request.getPassword()).isEqualTo("strongPass");
    }

    @Test
    @DisplayName("SignupRequest stores user data")
    void signupRequestData() {
        SignupRequest request = new SignupRequest();
        request.setEmail("signup@yoga.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPassword("password");

        assertThat(request.getEmail()).isEqualTo("signup@yoga.com");
        assertThat(request.getFirstName()).isEqualTo("New");
        assertThat(request.getLastName()).isEqualTo("User");
        assertThat(request.getPassword()).isEqualTo("password");
        assertThat(request.toString()).contains("signup@yoga.com");
    }
}
