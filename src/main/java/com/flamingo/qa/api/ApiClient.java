package com.flamingo.qa.api;

import com.flamingo.qa.config.Config;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class ApiClient {

    private static final HttpLoggingFilter LOGGING = new HttpLoggingFilter();

    protected RequestSpecification request() {
        return RestAssured.given()
                .baseUri(Config.baseUrl())
                .contentType(ContentType.JSON)
                .accept("application/json")
                .filter(LOGGING);
    }
}
