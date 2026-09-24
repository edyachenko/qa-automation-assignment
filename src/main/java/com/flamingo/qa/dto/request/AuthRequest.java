package com.flamingo.qa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@Builder
@With
public record AuthRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password) {
}
