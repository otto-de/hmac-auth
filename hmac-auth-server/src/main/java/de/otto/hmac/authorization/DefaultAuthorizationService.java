package de.otto.hmac.authorization;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.authentication.AuthenticationFilter.API_USERNAME;
import static java.util.Arrays.asList;

@Service
public class DefaultAuthorizationService implements AuthorizationService {
    
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAuthorizationService.class);

    private RoleRepository userRepository;

    private HttpServletRequest request;

    @Resource
    @Required
    public void setUserRepository(RoleRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Resource
    @Required
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void authorize(String... expectedRoles) {
        String username = getUsername(request);

        if (DISABLE_AUTHORIZATION_FOR_UNSIGNED_REQUESTS(username)) {
            return;
        }

        final Set<String> userRoles = userRepository.getRolesForUser(username);

        if (intersection(asList(expectedRoles), userRoles).isEmpty()) {
                throw new AuthorizationException(createErrorMessage(username, expectedRoles));
        }
    }

    private Set<String> intersection(Collection<String> allowedForRoles, Collection<String> rolesForUser) {
        final HashSet<String> set = new HashSet<>(allowedForRoles);
        set.retainAll(rolesForUser);
        return set;
    }

    private static boolean DISABLE_AUTHORIZATION_FOR_UNSIGNED_REQUESTS(String username) {
        return username == null;
    }

    private static String createErrorMessage(String username, String[] allowedForRoles) {
        String displayUser = StringUtils.isEmpty(username) ? "Anonymous user" : "["+username+"]";
        String displayAllowedRoles = StringUtils.join(allowedForRoles, ", ");
        return displayUser + " is not in one of these groups: [" + displayAllowedRoles + "].";
    }

    private static String getUsername(HttpServletRequest request) {
        Object username = request.getAttribute(API_USERNAME);
        return username!=null ? username.toString() : null;
    }

}
