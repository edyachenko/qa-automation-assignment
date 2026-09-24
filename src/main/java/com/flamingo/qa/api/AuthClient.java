package com.flamingo.qa.api;

import com.flamingo.qa.assertions.AuthResponseAssert;
import com.flamingo.qa.dto.request.AuthRequest;
import io.qameta.allure.Step;

public class AuthClient extends ApiClient {

    @Step("Create auth token")
    public AuthResponseAssert createToken(AuthRequest request) {
        return new AuthResponseAssert(request().body(request).post(Endpoints.AUTH));
    }
}
