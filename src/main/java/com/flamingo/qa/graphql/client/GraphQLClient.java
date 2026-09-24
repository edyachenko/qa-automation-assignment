package com.flamingo.qa.graphql.client;

import com.flamingo.qa.common.ApiClient;
import com.flamingo.qa.config.Config;
import com.flamingo.qa.graphql.assertions.MovieResponseAssert;
import com.flamingo.qa.graphql.assertions.MoviesResponseAssert;
import com.flamingo.qa.graphql.assertions.RawGraphQlResponseAssert;
import com.flamingo.qa.graphql.dto.GraphQlRequest;
import com.flamingo.qa.graphql.dto.MovieByIdVariables;
import com.flamingo.qa.graphql.dto.MoviesQueryVariables;
import com.flamingo.qa.graphql.generated.MovieResponseProjection;
import com.flamingo.qa.graphql.generated.UserResponseProjection;
import com.flamingo.qa.graphql.report.GraphQlAllureAttachments;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

public class GraphQLClient extends ApiClient {

    private static final String MOVIES =
            "query Movies($first: Int, $skip: Int) { movies(first: $first, skip: $skip, orderBy: createdAt_ASC) %s }";
    private static final String MOVIE_BY_ID =
            "query Movie($id: ID!) { movie(where: {id: $id}) %s }";

    public GraphQLClient() {
        super(Config.graphqlUrl(), true);
    }

    @Step("Query movies ({0})")
    public MoviesResponseAssert queryMovies(MoviesQueryVariables variables) {
        return queryMovies(variables, new MovieResponseProjection().id().title().slug());
    }

    @Step("Query movies with their publisher ({0})")
    public MoviesResponseAssert queryMoviesWithPublisher(MoviesQueryVariables variables) {
        return queryMovies(variables, new MovieResponseProjection()
                .id()
                .title()
                .publishedBy(new UserResponseProjection().name()));
    }

    @Step("Query movie by id \"{0}\"")
    public MovieResponseAssert queryMovieById(String id) {
        MovieResponseProjection fields = new MovieResponseProjection().id().title().slug();
        return new MovieResponseAssert(send(new GraphQlRequest(MOVIE_BY_ID.formatted(fields), new MovieByIdVariables(id))));
    }

    @Step("Execute raw GraphQL query: {0}")
    public RawGraphQlResponseAssert executeRaw(String query) {
        return new RawGraphQlResponseAssert(send(GraphQlRequest.of(query)));
    }

    @Step("Execute raw GraphQL query: {0} with variables {1}")
    public RawGraphQlResponseAssert executeRaw(String query, Map<String, Object> variables) {
        return new RawGraphQlResponseAssert(send(new GraphQlRequest(query, variables)));
    }

    private MoviesResponseAssert queryMovies(MoviesQueryVariables variables, MovieResponseProjection fields) {
        return new MoviesResponseAssert(send(new GraphQlRequest(MOVIES.formatted(fields), variables)));
    }

    private Response send(GraphQlRequest body) {
        GraphQlAllureAttachments.attach(body);
        return request().body(body).post();
    }
}
