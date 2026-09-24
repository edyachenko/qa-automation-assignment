package com.flamingo.qa.api;

import com.flamingo.qa.assertions.AuthResponseAssert;
import com.flamingo.qa.dto.request.AuthRequest;

public class AuthClient extends ApiClient {

    public AuthResponseAssert createToken(AuthRequest request) {
        return new AuthResponseAssert(request().body(request).post(Endpoints.AUTH));
    }
}
