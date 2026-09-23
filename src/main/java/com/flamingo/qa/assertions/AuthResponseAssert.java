package com.flamingo.qa.assertions;

import com.flamingo.qa.dto.response.AuthResponse;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthResponseAssert extends ResponseAssert<AuthResponseAssert> {

    public AuthResponseAssert(Response response) {
        super(response);
    }

    public AuthResponseAssert shouldHaveToken() {
        assertThat(token()).as("token").isNotBlank();
        return this;
    }

    public AuthResponseAssert shouldNotHaveToken() {
        assertThat(token()).as("token").isNull();
        return this;
    }

    public AuthResponseAssert shouldHaveReason(String expected) {
        assertThat(body(AuthResponse.class).reason()).as("reason").isEqualTo(expected);
        return this;
    }

    public String token() {
        return body(AuthResponse.class).token();
    }
}
