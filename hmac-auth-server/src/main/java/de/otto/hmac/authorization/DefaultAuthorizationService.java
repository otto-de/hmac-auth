package de.otto.hmac.authorization;

import de.otto.hmac.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.authentication.AuthenticationFilter.AUTHENTICATED_USERNAME;
import static java.lang.String.format;
import static java.util.Arrays.asList;

@Service
public class DefaultAuthorizationService implements AuthorizationService {

    private RoleRepository userRepository;

    private HttpServletRequest request;

    @Resource
    @Required
    public void setUserRepository(final RoleRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Resource
    @Required
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void authorize(final String... expectedRoles) {
        String username = getUsername(request);

        if (DISABLE_AUTHORIZATION_FOR_UNSIGNED_REQUESTS(username)) {
            return;
        }

        final Set<String> userRoles = userRepository.getRolesForUser(username);

        if (intersection(asList(expectedRoles), userRoles).isEmpty()) {
                throw new AuthorizationException(createErrorMessage(username, expectedRoles));
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

    private static String createErrorMessage(final String username, final String[] allowedForRoles) {
        final String displayUser = StringUtils.isNullOrEmpty(username) ? "Anonymous user" : "["+username+"]";
        return format("%s is not in one of these groups: %s.", displayUser, Arrays.toString(allowedForRoles));
    }

    private static String getUsername(final HttpServletRequest request) {
        final Object username = request.getAttribute(AUTHENTICATED_USERNAME);
        return username!=null ? username.toString() : null;
    }

}
