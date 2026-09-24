package com.flamingo.qa.graphql.assertions;

import com.flamingo.qa.graphql.generated.MovieQueryResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieResponseAssert extends GraphQlResponseAssert<MovieResponseAssert, MovieQueryResponse> {

    public MovieResponseAssert(Response response) {
        super(response, MovieQueryResponse.class);
    }

    @Step("Should have movie with id {0}")
    public MovieResponseAssert shouldHaveMovieId(String expected) {
        assertThat(getResult().movie()).as("movie").isNotNull();
        assertThat(getResult().movie().getId()).as("movie id").isEqualTo(expected);
        return this;
    }

    @Step("Should have no movie")
    public MovieResponseAssert shouldHaveNoMovie() {
        assertThat(getResult().getData()).as("GraphQL data").isNotNull();
        assertThat(getResult().movie()).as("movie").isNull();
        return this;
    }
}
