package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.config.TestTag;
import com.flamingo.qa.graphql.dto.MoviesQueryVariables;
import com.flamingo.qa.graphql.generated.Movie;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;

@Tag(TestTag.Names.GRAPHQL)
@Feature("GraphQL queries")
@DisplayName("GraphQL: Hygraph movies")
public class GraphQlTests  extends BaseGraphQlTest{


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

    @Nested
    @DisplayName("query movies")
    class Movies {

        @Test
        @DisplayName("respects the requested page limit")
        void queryMoviesListRespectsLimit() {
            graphqlClient.queryMovies(new MoviesQueryVariables(3))
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .shouldHaveMovieCount(3);
        }

        @Test
        @DisplayName("skip variable moves the page forward without overlapping the previous page")
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
        @DisplayName("resolves the nested publisher name across the User type")
        void queryMoviesWithPublisherReturnsNestedPublisherName() {
            graphqlClient.queryMoviesWithPublisher(new MoviesQueryVariables(3))
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .shouldHaveMovieCount(3)
                    .shouldHavePublisherNameOnEveryMovie();
        }

        @Test
        @DisplayName("returns a non-blank slug on every movie")
        void queryMoviesReturnsSlugOnEveryMovie() {
            graphqlClient.queryMovies(new MoviesQueryVariables(5))
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .satisfies(response -> assertThat(response.movies())
                            .extracting(Movie::getSlug)
                            .allSatisfy(slug -> assertThat(slug).isNotBlank()));
        }
    }

    @Nested
    @DisplayName("query movie by id")
    class MovieById {

        @Test
        @DisplayName("returns exactly the movie with an existing id")
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
        @DisplayName("returns HTTP 200 with a null movie and no errors for a non-existent id")
        void queryMovieByNonExistentIdReturnsNullMovie() {
            graphqlClient.queryMovieById("non-existent-id")
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .shouldHaveNoMovie();
        }
    }

    @Nested
    @DisplayName("variables validation")
    class VariablesValidation {

        @Test
        @DisplayName("missing required variable returns HTTP 400 naming that variable and no data")
        void missingRequiredVariableIsRejected() {
            graphqlClient.executeRaw("query Movie($id: ID!) { movie(where: {id: $id}) { id } }", Map.of())
                    .shouldHaveStatus(SC_BAD_REQUEST)
                    .shouldHaveErrorContaining("variable 'id' must be defined")
                    .shouldHaveNoData();
        }

        @Test
        @DisplayName("variable of a wrong type returns HTTP 400 with a type error and no data")
        void wrongVariableTypeIsRejected() {
            graphqlClient.executeRaw("query Movies($first: Int) { movies(first: $first) { id } }", Map.of("first", "abc"))
                    .shouldHaveStatus(SC_BAD_REQUEST)
                    .shouldHaveErrorContaining("expected type 'Int' for variable 'first'")
                    .shouldHaveNoData();
        }
    }

    @Nested
    @DisplayName("invalid queries")
    class InvalidQueries {

        @Test
        @DisplayName("malformed query returns HTTP 400 with a parse error and no data")
        void malformedQueryReturnsParseErrorWithoutData() {
            graphqlClient.executeRaw("{ movies { id ")
                    .shouldHaveStatus(SC_BAD_REQUEST)
                    .shouldHaveErrorContaining("ParseError")
                    .shouldHaveNoData();
        }

        @Test
        @DisplayName("field missing from the schema returns HTTP 400 with a validation error naming that field")
        void unknownFieldReturnsValidationError() {
            graphqlClient.executeRaw("{ movies { id notExistingField } }")
                    .shouldHaveStatus(SC_BAD_REQUEST)
                    .shouldHaveErrorContaining("field 'notExistingField' is not defined in 'Movie'")
                    .shouldHaveNoData();
        }
    }

    @Nested
    @DisplayName("query language features")
    class QueryLanguageFeatures {

        @Test
        @DisplayName("aliases return the same field twice with different arguments under their own names")
        void aliasesReturnEachSelectionUnderItsOwnName() {
            graphqlClient.executeRaw("{ firstTwo: movies(first: 2) { id } nextTwo: movies(first: 2, skip: 2) { id } }")
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .shouldHaveDataKeys("firstTwo", "nextTwo")
                    .shouldHaveDataValue("firstTwo.size()", 2)
                    .shouldHaveDataValue("nextTwo.size()", 2)
                    .satisfies(response -> assertThat(response.getData().get("nextTwo"))
                            .as("nextTwo page")
                            .isNotEqualTo(response.getData().get("firstTwo")));
        }

        @Test
        @DisplayName("introspection is enabled on the public endpoint and exposes the Query root type")
        void introspectionIsEnabled() {
            graphqlClient.executeRaw("{ __schema { queryType { name } } }")
                    .shouldHaveStatus(SC_OK)
                    .shouldHaveNoErrors()
                    .shouldHaveDataValue("__schema.queryType.name", "Query");
        }
    }
}
