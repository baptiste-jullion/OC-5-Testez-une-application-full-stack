package com.openclassrooms.starterjwt.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class AuthEntryPointJwtTest {

    private final AuthEntryPointJwt entryPoint = new AuthEntryPointJwt();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("commence writes a JSON body with error details")
    void commence_shouldWriteJsonBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServletPath("/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new AuthenticationCredentialsNotFoundException("Bad credentials"));

        assertThat(response.getStatus()).isEqualTo(MockHttpServletResponse.SC_UNAUTHORIZED);
        JsonNode payload = mapper.readTree(response.getContentAsString());
        assertThat(payload.get("status").asInt()).isEqualTo(MockHttpServletResponse.SC_UNAUTHORIZED);
        assertThat(payload.get("error").asText()).isEqualTo("Unauthorized");
        assertThat(payload.get("message").asText()).contains("Bad credentials");
        assertThat(payload.get("path").asText()).isEqualTo("/api/test");
    }
}
