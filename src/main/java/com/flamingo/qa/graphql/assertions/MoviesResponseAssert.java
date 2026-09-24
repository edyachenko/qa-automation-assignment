package com.flamingo.qa.graphql.assertions;

import com.flamingo.qa.graphql.generated.Movie;
import com.flamingo.qa.graphql.generated.MoviesQueryResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MoviesResponseAssert extends GraphQlResponseAssert<MoviesResponseAssert, MoviesQueryResponse> {

    public MoviesResponseAssert(Response response) {
        super(response, MoviesQueryResponse.class);
    }

    @Step("Should have {0} movies")
    public MoviesResponseAssert shouldHaveMovieCount(int expected) {
        assertThat(movies()).as("movies").hasSize(expected);
        return this;
    }

    @Step("Should not contain any of the movies {0}")
    public MoviesResponseAssert shouldNotContainMovieIds(List<String> ids) {
        assertThat(movieIds()).as("movie ids").doesNotContainAnyElementsOf(ids);
        return this;
    }

    @Step("Should have a publisher name on every movie")
    public MoviesResponseAssert shouldHavePublisherNameOnEveryMovie() {
        assertThat(movies())
                .as("movies")
                .isNotEmpty()
                .allSatisfy(movie -> assertThat(movie.getPublishedBy())
                        .as("publishedBy of movie %s", movie.getId())
                        .isNotNull()
                        .satisfies(user -> assertThat(user.getName()).as("publisher name").isNotBlank()));
        return this;
    }

    public List<String> movieIds() {
        return movies().stream().map(Movie::getId).toList();
    }

    public String firstMovieId() {
        assertThat(movies()).as("movies").isNotEmpty();
        return movies().get(0).getId();
    }

    private List<Movie> movies() {
        return getResult().movies();
    }
}
