package com.flamingo.qa.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("token") String token,
        @JsonProperty("reason") String reason) {
}
