package com.flamingo.qa.graphql.client;

import com.flamingo.qa.graphql.assertions.ProductResponseAssert;
import com.flamingo.qa.graphql.assertions.ProductsResponseAssert;
import com.flamingo.qa.graphql.assertions.RawGraphQlResponseAssert;
import com.flamingo.qa.graphql.dto.ProductsQueryVariables;

public class GraphQLClient {

    public ProductsResponseAssert queryProducts(ProductsQueryVariables variables) {
        throw new UnsupportedOperationException();
    }

    public ProductsResponseAssert queryProductsWithCategory(ProductsQueryVariables variables) {
        throw new UnsupportedOperationException();
    }

    public ProductResponseAssert queryProductById(String id) {
        throw new UnsupportedOperationException();
    }

    public RawGraphQlResponseAssert executeRaw(String query) {
        throw new UnsupportedOperationException();
    }
}
