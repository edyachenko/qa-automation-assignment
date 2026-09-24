package com.flamingo.qa.assertions.graphql;

import io.restassured.response.Response;

public class ProductResponseAssert extends GraphQlResponseAssert<ProductResponseAssert> {

    public ProductResponseAssert(Response response) {
        super(response);
    }

    public ProductResponseAssert shouldHaveProductId(String expected) {
        throw new UnsupportedOperationException();
    }

    public ProductResponseAssert shouldHaveNullData() {
        throw new UnsupportedOperationException();
    }
}
