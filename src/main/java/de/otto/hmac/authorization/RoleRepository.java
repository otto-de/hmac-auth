package de.otto.hmac.authorization;

import java.util.Set;

public interface RoleRepository {

    /**
     * Returns all roles of the specified user.
     *
     * @param user the username.
     * @return set of roles associated with the user.
     */
    Set<String> getRolesForUser(String user);

    /**
     * Returns true if the user has the given role, false otherwise.
     *
     * @param user the username.
     * @param role the role to check.
     * @return boolean
     */
    boolean hasRole(String user, String role);
}
