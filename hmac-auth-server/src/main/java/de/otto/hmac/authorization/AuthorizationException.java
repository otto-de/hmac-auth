package de.otto.hmac.authorization;

import static java.lang.String.format;

public class AuthorizationException extends RuntimeException {

    private static final String errorMessage = "%s is not in one of the required security groups.";

    public AuthorizationException(String message) {
        super(format(errorMessage, message));
    }

}
