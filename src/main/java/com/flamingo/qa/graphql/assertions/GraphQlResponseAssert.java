package com.flamingo.qa.graphql.assertions;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flamingo.qa.common.ResponseAssert;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLError;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class GraphQlResponseAssert<SELF extends GraphQlResponseAssert<SELF, R>, R extends GraphQLResult<?>>
        extends ResponseAssert<SELF> {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final Class<R> resultType;

    @Getter(value = AccessLevel.PROTECTED, lazy = true)
    private final R result = parse();

    protected GraphQlResponseAssert(Response response, Class<R> resultType) {
        super(response);
        this.resultType = resultType;
    }

    @Step("Should have no GraphQL errors")
    public SELF shouldHaveNoErrors() {
        assertThat(getResult().getErrors())
                .as("GraphQL errors, response body: %s", response.asString())
                .isNullOrEmpty();
        return self();
    }

    @Step("Should have a GraphQL error containing \"{0}\"")
    public SELF shouldHaveErrorContaining(String expectedMessagePart) {
        assertThat(getResult().getErrors())
                .as("GraphQL errors, response body: %s", response.asString())
                .isNotEmpty()
                .extracting(GraphQLError::getMessage)
                .anySatisfy(message -> assertThat(message).contains(expectedMessagePart));
        return self();
    }

    @Step("Should have no data")
    public SELF shouldHaveNoData() {
        assertThat(getResult().getData()).as("GraphQL data").isNull();
        return self();
    }

    @Step("Should satisfy a custom check on the parsed response")
    public SELF satisfies(Consumer<R> check) {
        check.accept(getResult());
        return self();
    }

    @SneakyThrows
    private R parse() {
        return MAPPER.readValue(response.asString(), resultType);
    }

    @SuppressWarnings("unchecked")
    private SELF self() {
        return (SELF) this;
    }
}
