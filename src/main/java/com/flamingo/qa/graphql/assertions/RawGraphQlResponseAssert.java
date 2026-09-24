package com.flamingo.qa.graphql.assertions;

import io.restassured.response.Response;

public class RawGraphQlResponseAssert extends GraphQlResponseAssert<RawGraphQlResponseAssert> {

    public RawGraphQlResponseAssert(Response response) {
        super(response);
    }
}
