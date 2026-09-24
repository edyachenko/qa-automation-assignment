package com.flamingo.qa.common;

import com.flamingo.qa.config.Config;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class ApiClient {

    private static final HttpLoggingFilter LOGGING_WITH_BODIES = new HttpLoggingFilter(true);
    private static final HttpLoggingFilter LOGGING_WITHOUT_BODIES = new HttpLoggingFilter(false);
    private static final AllureRestAssured ALLURE = new AllureRestAssured();

    private final HttpLoggingFilter logging;

    protected ApiClient() {
        this(true);
    }

    protected ApiClient(boolean logBodies) {
        this.logging = logBodies ? LOGGING_WITH_BODIES : LOGGING_WITHOUT_BODIES;
    }

    protected RequestSpecification request() {
        return RestAssured.given()
                .baseUri(Config.baseUrl())
                .contentType(ContentType.JSON)
                .accept("application/json")
                .filter(logging)
                .filter(ALLURE);
    }
}
