package de.otto.hmac.authorization;


public interface AuthorizationService {

    void authorize(String... allowedForRoles);

}
