package com.flamingo.qa.tests;

import com.flamingo.qa.dto.request.AuthRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.SC_OK;

@DisplayName("POST /auth")
class AuthTests extends BaseApiTest {

    @Test
    @DisplayName("returns a token for valid credentials")
    void returnsTokenForValidCredentials(AuthRequest credentials) {
        authClient.createToken(credentials)
                .shouldHaveStatus(SC_OK)
                .shouldHaveToken();
    }

    @Test
    @DisplayName("returns 'Bad credentials' and no token for a wrong password")
    void returnsBadCredentialsForWrongPassword(AuthRequest credentials) {
        authClient.createToken(new AuthRequest(credentials.username(), "wrong-password"))
                .shouldHaveStatus(SC_OK)
                .shouldNotHaveToken()
                .shouldHaveReason("Bad credentials");
    }
}
