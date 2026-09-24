package com.flamingo.qa.api;

import com.flamingo.qa.assertions.graphql.ProductResponseAssert;
import com.flamingo.qa.assertions.graphql.ProductsResponseAssert;
import com.flamingo.qa.assertions.graphql.RawGraphQlResponseAssert;
import com.flamingo.qa.dto.graphql.ProductsQueryVariables;

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
