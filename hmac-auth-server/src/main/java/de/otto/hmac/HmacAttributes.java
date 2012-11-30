package de.otto.hmac;

/**
 * Names of the request attributes used by hmac-auth.
 *
 * @author Guido Steinacker
 * @since 30.11.12
 */
public class HmacAttributes {

    /**
     * Internally: the name of the authenticated user.
     */
    public static final String AUTHENTICATED_USERNAME = "authenticated-username";
    /**
     * Name of the request-header attribute used to submit the signature.
     */
    public static String X_HMAC_AUTH_SIGNATURE = "x-hmac-auth-signature";
    /**
     * Name of the request-header attribute used to submit the timestamp.
     */
    public static String X_HMAC_AUTH_DATE = "x-hmac-auth-date";

}
