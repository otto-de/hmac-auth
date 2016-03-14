package de.otto.hmac.repository;

import com.google.gson.Gson;
import de.otto.hmac.authentication.UserRepository;
import de.otto.hmac.authorization.RoleRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PropertyUserRepository implements UserRepository, RoleRepository {

    private final ConcurrentMap<String, String> userToKey = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> userToRole = new ConcurrentHashMap<>();

    public PropertyUserRepository(final String credentialsJson) {
        loadCredentialsFromJson(credentialsJson, userToKey, userToRole);
    }

    @Override
    public String getKey(final String username) {
        return userToKey.get(username);
    }

    @Override
    public boolean hasRole(final String user, final String role) {
        return getRolesForUser(user).contains(role);
    }

    @Override
    public Set<String> getRolesForUser(final String user) {
        final Set<String> roles = new HashSet<>();

        if (user != null) {
            final Set<String> userRoles = userToRole.get(user);
            if (userRoles != null) {
                roles.addAll(userRoles);
            }
        }

        return roles;
    }

    private void loadCredentialsFromJson(final String credentialsJson,
                                         final ConcurrentMap<String, String> userToKey,
                                         final ConcurrentMap<String, Set<String>> userToRole) {
        final UserCredentialsList userCredentialsList = new Gson().fromJson(credentialsJson, UserCredentialsList.class);

        for (UserCredentials userCredentials : userCredentialsList) {
            userToKey.put(userCredentials.getUser(), userCredentials.getPassword());
            userToRole.put(userCredentials.getUser(), userCredentials.getRoles());
        }
    }
}
