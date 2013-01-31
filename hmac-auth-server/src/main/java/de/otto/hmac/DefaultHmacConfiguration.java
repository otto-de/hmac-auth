package de.otto.hmac;

import de.otto.hmac.authorization.HmacConfiguration;
import org.springframework.stereotype.Component;

@Component
public class DefaultHmacConfiguration implements HmacConfiguration {

    @Override
    public boolean disableAuthorizationForUnsignedRequests() {
        return false;
    }
}
