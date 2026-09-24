package com.flamingo.qa.graphql.assertions;

import com.flamingo.qa.graphql.dto.RawQueryResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class RawGraphQlResponseAssert extends GraphQlResponseAssert<RawGraphQlResponseAssert, RawQueryResponse> {

    public RawGraphQlResponseAssert(Response response) {
        super(response, RawQueryResponse.class);
    }

    @Step("Should have data with exactly the keys {0}")
    public RawGraphQlResponseAssert shouldHaveDataKeys(String... keys) {
        assertThat(getResult().getData()).as("GraphQL data").containsOnlyKeys(keys);
        return this;
    }

    @Step("Should have data.{0} equal to {1}")
    public RawGraphQlResponseAssert shouldHaveDataValue(String path, Object expected) {
        assertThat(response.jsonPath().<Object>get("data." + path)).as("data.%s", path).isEqualTo(expected);
        return this;
    }
}
