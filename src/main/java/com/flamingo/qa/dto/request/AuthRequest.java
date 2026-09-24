package com.flamingo.qa.dto.request;

import lombok.Builder;
import lombok.With;

@Builder
@With
public record AuthRequest(String username, String password) {
}
