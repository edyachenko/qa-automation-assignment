package com.flamingo.qa.tests;

import com.flamingo.qa.dto.request.AuthRequest;
import com.flamingo.qa.extension.AuthDataExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.apache.http.HttpStatus.SC_OK;

@DisplayName("POST /auth")
class AuthTests extends BaseApiTest {

    @Test
    @DisplayName("returns a token for valid credentials")
    @ExtendWith(AuthDataExtension.class)
    void validCredentialsReturnToken(AuthRequest credentials) {
        authClient.createToken(credentials)
                .shouldHaveStatus(SC_OK)
                .shouldHaveToken();
    }

    @Test
    @DisplayName("returns 'Bad credentials' and no token for a wrong password")
    @ExtendWith(AuthDataExtension.class)
    void wrongPasswordReturnsBadCredentialsWithoutToken(AuthRequest credentials) {
        authClient.createToken(credentials.withPassword("wrong-password"))
                .shouldHaveStatus(SC_OK)
                .shouldNotHaveToken()
                .shouldHaveReason("Bad credentials");
    }
}
