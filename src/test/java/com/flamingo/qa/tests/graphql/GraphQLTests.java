package com.flamingo.qa.tests.graphql;

import com.flamingo.qa.config.TestTag;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    private final GraphQLClient graphqlClient = new GraphQLClient();

    @Test
    @DisplayName("Products list respects the requested page limit")
    void queryProductsListRespectsLimit() {
        graphqlClient.queryProducts(new ProductsQueryVariables(3))
                .shouldHaveNoErrors()
                .shouldHaveItemCount(3);
    }

    @Test
    @DisplayName("Querying a product by a non-existent id returns null data without errors")
    void queryProductByNonExistentIdReturnsNullData() {
        graphqlClient.queryProductById("non-existent-id")
                .shouldHaveNoErrors()
                .shouldHaveNullData();
    }

    @Test
    @DisplayName("Querying a product by an existing id returns exactly that product")
    void queryProductByExistingIdReturnsThatProduct() {
        String existingId = graphqlClient.queryProducts(new ProductsQueryVariables(1))
                .shouldHaveNoErrors()
                .firstProductId();

        graphqlClient.queryProductById(existingId)
                .shouldHaveNoErrors()
                .shouldHaveProductId(existingId);
    }

    @Test
    @DisplayName("Skip variable moves the page forward without overlapping the previous page")
    void queryProductsWithSkipVariableReturnsNextPage() {
        List<String> firstPage = graphqlClient.queryProducts(new ProductsQueryVariables(2, 0))
                .shouldHaveNoErrors()
                .productIds();

        graphqlClient.queryProducts(new ProductsQueryVariables(2, 2))
                .shouldHaveNoErrors()
                .shouldHaveItemCount(2)
                .shouldNotContainProductIds(firstPage);
    }

    @Test
    @DisplayName("Product query resolves nested fields of a related type")
    void queryProductWithNestedRelationReturnsRelatedFields() {
        graphqlClient.queryProductsWithCategory(new ProductsQueryVariables(3))
                .shouldHaveNoErrors()
                .shouldHaveItemCount(3)
                .shouldHaveCategoryNameOnEveryProduct();
    }

    @Test
    @DisplayName("Malformed query returns a syntax error and no data")
    void malformedQueryReturnsSyntaxErrorWithoutData() {
        graphqlClient.executeRaw("{ products { id ")
                .shouldHaveErrorContaining("Syntax Error")
                .shouldHaveNoData();
    }

    @Test
    @DisplayName("Requesting a field missing from the schema returns a validation error naming that field")
    void unknownFieldReturnsValidationError() {
        graphqlClient.executeRaw("{ products { id notExistingField } }")
                .shouldHaveErrorContaining("notExistingField")
                .shouldHaveNoData();
    }
}
