package de.otto.hmac.authorization;

public interface HmacConfiguration {

    /**
     * Returning true allows requests without any signature to always be allowed.
     * This basically disables the authorization, so it should be used in Tests only,
     * or in an intermediate phase to introduce the HMAC in a server while
     * clients are not able to properly sign their requests.
     * @return true iff authorization shall be disabled
     */
    public boolean disableAuthorizationForUnsignedRequests();

}
