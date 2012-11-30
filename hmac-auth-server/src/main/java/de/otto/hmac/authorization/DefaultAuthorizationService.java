package de.otto.hmac.authorization;

import de.otto.hmac.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@Service
public class DefaultAuthorizationService implements AuthorizationService {

    private RoleRepository userRepository;

    @Resource
    @Required
    public void setUserRepository(final RoleRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void authorize(final String userName, final Set<String> expectedRoles) {

        if (DISABLE_AUTHORIZATION_FOR_UNSIGNED_REQUESTS(userName)) {
            return;
        }

        final Set<String> userRoles = userRepository.getRolesForUser(userName);

        if (intersection(expectedRoles, userRoles).isEmpty()) {
                throw new AuthorizationException(createErrorMessage(userName, expectedRoles));
        }
    }

    private Set<String> intersection(final Collection<String> allowedForRoles, final Collection<String> rolesForUser) {
        final HashSet<String> set = new HashSet<>(allowedForRoles);
        set.retainAll(rolesForUser);
        return set;
    }

    private static boolean DISABLE_AUTHORIZATION_FOR_UNSIGNED_REQUESTS(String username) {
        return username == null;
    }

    private static String createErrorMessage(final String username, final Set<String> allowedForRoles) {
        final String displayUser = StringUtils.isNullOrEmpty(username) ? "Anonymous user" : "["+username+"]";
        return format("%s is not in one of these groups: %s.", displayUser, allowedForRoles);
    }

}
