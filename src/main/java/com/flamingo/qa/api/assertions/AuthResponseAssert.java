package com.flamingo.qa.api.assertions;

import com.flamingo.qa.common.ResponseAssert;
import com.flamingo.qa.api.dto.response.AuthResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthResponseAssert extends ResponseAssert<AuthResponseAssert> {

    public AuthResponseAssert(Response response) {
        super(response);
    }

    @Step("Should have a token")
    public AuthResponseAssert shouldHaveToken() {
        assertThat(token()).as("token").isNotBlank();
        return this;
    }

    @Step("Should not have a token")
    public AuthResponseAssert shouldNotHaveToken() {
        assertThat(token()).as("token").isNull();
        return this;
    }

    @Step("Should have reason {0}")
    public AuthResponseAssert shouldHaveReason(String expected) {
        assertThat(body(AuthResponse.class).reason()).as("reason").isEqualTo(expected);
        return this;
    }

    public String token() {
        return body(AuthResponse.class).token();
    }
}
