package com.flamingo.qa.common;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class ResponseAssert<SELF extends ResponseAssert<SELF>> {

    protected final Response response;

    protected ResponseAssert(Response response) {
        this.response = response;
    }

    @Step("Should have HTTP status {0}")
    public SELF shouldHaveStatus(int expected) {
        assertThat(response.statusCode())
                .as("HTTP status, response body: %s", response.asString())
                .isEqualTo(expected);
        return self();
    }

    public boolean isSuccessful() {
        return response.statusCode() == SC_OK;
    }

    protected <T> T body(Class<T> type) {
        return response.as(type);
    }

    @SuppressWarnings("unchecked")
    private SELF self() {
        return (SELF) this;
    }
}
