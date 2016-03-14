package de.otto.hmac;

import de.otto.hmac.authorization.HmacConfiguration;

public class DefaultHmacConfiguration implements HmacConfiguration {

    @Override
    public boolean disableAuthorizationForUnsignedRequests() {
        return false;
    }
}
