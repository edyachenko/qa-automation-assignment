package com.flamingo.qa.graphql.assertions;

import com.flamingo.qa.common.ResponseAssert;
import io.restassured.response.Response;

public abstract class GraphQlResponseAssert<SELF extends GraphQlResponseAssert<SELF>> extends ResponseAssert<SELF> {

    protected GraphQlResponseAssert(Response response) {
        super(response);
    }

    public SELF shouldHaveNoErrors() {
        throw new UnsupportedOperationException();
    }

    public SELF shouldHaveErrorContaining(String expectedMessagePart) {
        throw new UnsupportedOperationException();
    }

    public SELF shouldHaveNoData() {
        throw new UnsupportedOperationException();
    }
}
