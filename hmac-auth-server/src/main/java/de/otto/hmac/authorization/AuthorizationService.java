package de.otto.hmac.authorization;


import java.util.Set;

public interface AuthorizationService {

    void authorize(String userName, Set<String> allowedForRoles);

}
