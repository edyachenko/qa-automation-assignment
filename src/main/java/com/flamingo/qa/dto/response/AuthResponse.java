package com.flamingo.qa.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("token") String token,
        @JsonProperty("reason") String reason) {
}
