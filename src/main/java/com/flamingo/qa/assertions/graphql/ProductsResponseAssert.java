package com.flamingo.qa.assertions.graphql;

import io.restassured.response.Response;

import java.util.List;

public class ProductsResponseAssert extends GraphQlResponseAssert<ProductsResponseAssert> {

    public ProductsResponseAssert(Response response) {
        super(response);
    }

    public ProductsResponseAssert shouldHaveItemCount(int expected) {
        throw new UnsupportedOperationException();
    }

    public ProductsResponseAssert shouldNotContainProductIds(List<String> ids) {
        throw new UnsupportedOperationException();
    }

    public ProductsResponseAssert shouldHaveCategoryNameOnEveryProduct() {
        throw new UnsupportedOperationException();
    }

    public String firstProductId() {
        throw new UnsupportedOperationException();
    }

    public List<String> productIds() {
        throw new UnsupportedOperationException();
    }
}
