package com.openclassrooms.starterjwt.payload.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PayloadResponseTest {

    @Test
    @DisplayName("JwtResponse exposes token information")
    void jwtResponseValues() {
        JwtResponse response = new JwtResponse("tokenValue", 7L, "user", "First", "Last", true);

        assertThat(response.getToken()).isEqualTo("tokenValue");
        assertThat(response.getType()).isEqualTo("Bearer");
        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getUsername()).isEqualTo("user");
        assertThat(response.getFirstName()).isEqualTo("First");
        assertThat(response.getLastName()).isEqualTo("Last");
        assertThat(response.getAdmin()).isTrue();

        response.setToken("newToken");
        response.setType("Custom");
        assertThat(response.getToken()).isEqualTo("newToken");
        assertThat(response.getType()).isEqualTo("Custom");
    }

    @Test
    @DisplayName("MessageResponse carries text message")
    void messageResponseCarriesMessage() {
        MessageResponse response = new MessageResponse("Created");

        assertThat(response.getMessage()).isEqualTo("Created");

        response.setMessage("Updated");
        assertThat(response.getMessage()).isEqualTo("Updated");
    }
}
