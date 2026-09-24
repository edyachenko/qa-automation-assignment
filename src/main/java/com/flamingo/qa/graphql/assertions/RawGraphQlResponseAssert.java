package com.flamingo.qa.graphql.assertions;

import com.flamingo.qa.graphql.dto.RawQueryResponse;
import io.restassured.response.Response;

public class RawGraphQlResponseAssert extends GraphQlResponseAssert<RawGraphQlResponseAssert, RawQueryResponse> {

    public RawGraphQlResponseAssert(Response response) {
        super(response, RawQueryResponse.class);
    }
}
