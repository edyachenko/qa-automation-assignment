package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.graphql.dto.MoviesQueryVariables;
import com.flamingo.qa.graphql.generated.Movie;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Tag(TestTag.Names.GRAPHQL)
@Feature("GraphQL queries")
public class GraphQLTests  extends BaseGraphql{


    /*
    Video
    GraphQL Positive
        Query a list with pagination/limit
        Query a single entity by ID.
        A query that uses GraphQL variables (not string interpolation).
        A query that uses a fragment or nested fields across types (e.g. movie → publishedBy → name)
    GraphQL Negative
        Invalid ID (non-existent) — assert response shape (GraphQL typically returns HTTP 200 with data: null or an errors array; verify which).
        Malformed query (syntax error) — assert errors[].message and absence of data.
        Requesting a non-existent field — assert validation error.

     */

    @Test
    @DisplayName("Movies list respects the requested page limit")
    void queryMoviesListRespectsLimit() {
        graphqlClient.queryMovies(new MoviesQueryVariables(3))
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .shouldHaveMovieCount(3);
    }

    @Test
    @DisplayName("Querying a movie by a non-existent id returns HTTP 200 with a null movie and no errors")
    void queryMovieByNonExistentIdReturnsNullMovie() {
        graphqlClient.queryMovieById("non-existent-id")
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .shouldHaveNoMovie();
    }

    @Test
    @DisplayName("Querying a movie by an existing id returns exactly that movie")
    void queryMovieByExistingIdReturnsThatMovie() {
        String existingId = graphqlClient.queryMovies(new MoviesQueryVariables(1))
                .shouldHaveNoErrors()
                .firstMovieId();

        graphqlClient.queryMovieById(existingId)
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .shouldHaveMovieId(existingId);
    }

    @Test
    @DisplayName("Skip variable moves the page forward without overlapping the previous page")
    void queryMoviesWithSkipVariableReturnsNextPage() {
        List<String> firstPage = graphqlClient.queryMovies(new MoviesQueryVariables(2, 0))
                .shouldHaveNoErrors()
                .movieIds();

        graphqlClient.queryMovies(new MoviesQueryVariables(2, 2))
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .shouldHaveMovieCount(2)
                .shouldNotContainMovieIds(firstPage);
    }

    @Test
    @DisplayName("Every movie in the list has a non-blank slug")
    void queryMoviesReturnsSlugOnEveryMovie() {
        graphqlClient.queryMovies(new MoviesQueryVariables(5))
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .satisfies(response -> assertThat(response.movies())
                        .extracting(Movie::getSlug)
                        .allSatisfy(slug -> assertThat(slug).isNotBlank()));
    }

    @Test
    @DisplayName("Movie query resolves the nested publisher name across the User type")
    void queryMoviesWithPublisherReturnsNestedPublisherName() {
        graphqlClient.queryMoviesWithPublisher(new MoviesQueryVariables(3))
                .shouldHaveStatus(SC_OK)
                .shouldHaveNoErrors()
                .shouldHaveMovieCount(3)
                .shouldHavePublisherNameOnEveryMovie();
    }

    @Test
    @DisplayName("Malformed query returns HTTP 400 with a parse error and no data")
    void malformedQueryReturnsParseErrorWithoutData() {
        graphqlClient.executeRaw("{ movies { id ")
                .shouldHaveStatus(SC_BAD_REQUEST)
                .shouldHaveErrorContaining("ParseError")
                .shouldHaveNoData();
    }

    @Test
    @DisplayName("Requesting a field missing from the schema returns HTTP 400 with a validation error naming that field")
    void unknownFieldReturnsValidationError() {
        graphqlClient.executeRaw("{ movies { id notExistingField } }")
                .shouldHaveStatus(SC_BAD_REQUEST)
                .shouldHaveErrorContaining("field 'notExistingField' is not defined in 'Movie'")
                .shouldHaveNoData();
    }
}
