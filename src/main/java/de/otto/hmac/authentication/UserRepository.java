package de.otto.hmac.authentication;

public interface UserRepository {

    String getKey(String username);

}
