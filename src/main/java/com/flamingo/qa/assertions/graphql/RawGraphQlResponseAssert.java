package com.flamingo.qa.assertions.graphql;

import io.restassured.response.Response;

public class RawGraphQlResponseAssert extends GraphQlResponseAssert<RawGraphQlResponseAssert> {

    public RawGraphQlResponseAssert(Response response) {
        super(response);
    }
}
