package de.otto.hmac.authentication;

import static de.otto.hmac.authentication.AuthenticationResult.Status.FAIL;
import static de.otto.hmac.authentication.AuthenticationResult.Status.SUCCESS;

public class AuthenticationResult {

    public static AuthenticationResult fail() {
        return new AuthenticationResult(FAIL, null);
    }

    public static AuthenticationResult success(String username) {
        return new AuthenticationResult(SUCCESS, username);
    }

    public enum Status {
        SUCCESS, FAIL;
    }

    private final Status status;
    private final String username;

    public AuthenticationResult(Status status, String username) {
        this.status = status;
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }


}
