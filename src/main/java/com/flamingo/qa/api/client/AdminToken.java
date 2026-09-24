package com.flamingo.qa.api.client;

import com.flamingo.qa.config.Config;
import com.flamingo.qa.api.dto.request.AuthRequest;
import lombok.Getter;

import static org.apache.http.HttpStatus.SC_OK;

public enum AdminToken {

    INSTANCE;

    @Getter(lazy = true)
    private final String value = requestToken();

    private String requestToken() {
        return new AuthClient().createToken(AuthRequest.builder()
                        .username(Config.username())
                        .password(Config.password())
                        .build())
                .shouldHaveStatus(SC_OK)
                .shouldHaveToken()
                .token();
    }
}
