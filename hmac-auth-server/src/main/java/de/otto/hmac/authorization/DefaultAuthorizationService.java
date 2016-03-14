package de.otto.hmac.authorization;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.StringUtils.isNullOrEmpty;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

public class DefaultAuthorizationService implements AuthorizationService {

    private final RoleRepository userRepository;
    private final HmacConfiguration hmacConfiguration;

    private static final Logger LOG = getLogger(DefaultAuthorizationService.class);
    private static final String ANONYMOUS_USER = "Anonymous user";

    public DefaultAuthorizationService(final RoleRepository userRepository,
                                       final HmacConfiguration hmacConfiguration) {
        this.userRepository = userRepository;
        this.hmacConfiguration = hmacConfiguration;
    }

    @Override
    public void authorize(final String userName, final Set<String> expectedRoles) {
        if (doNotCheck(userName)) {
            return;
        }

        final Set<String> userRoles = userRepository.getRolesForUser(userName);

        if (intersection(expectedRoles, userRoles).isEmpty()) {
            final String displayUser = formatUsername(userName);
            logAuthorizationFailure(displayUser, expectedRoles);
            throw new AuthorizationException(displayUser);
        }
    }

    private boolean doNotCheck(String username) {
        return hmacConfiguration.disableAuthorizationForUnsignedRequests() && username == null;
    }


    private Set<String> intersection(final Collection<String> allowedForRoles, final Collection<String> rolesForUser) {
        final HashSet<String> set = new HashSet<>(allowedForRoles);
        set.retainAll(rolesForUser);
        return set;
    }

    private void logAuthorizationFailure(final String displayUser, final Set<String> allowedForRoles) {
        String message = format("%s is not in one of these groups: %s.", displayUser, allowedForRoles);
        LOG.info(message);
    }

    private String formatUsername(String username) {

        if (isNullOrEmpty(username)) {
            username = ANONYMOUS_USER;
        }
        return "[" + username + "]";
    }
}
