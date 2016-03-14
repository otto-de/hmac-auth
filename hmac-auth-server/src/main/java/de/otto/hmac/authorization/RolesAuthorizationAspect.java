package de.otto.hmac.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.HmacAttributes.AUTHENTICATED_USERNAME;
import static java.util.Arrays.asList;

@Aspect
public class RolesAuthorizationAspect {

    private final AuthorizationService authorizationService;
    private final HttpServletRequest request;

    public RolesAuthorizationAspect(final AuthorizationService authorizationService,
                                    final HttpServletRequest request) {
        this.authorizationService = authorizationService;
        this.request = request;
    }

    @Before("@annotation(allowedForRoles)")
    public void assertAuthorized(JoinPoint jp, AllowedForRoles allowedForRoles) {
        final Set<String> roles = new HashSet<>(asList(allowedForRoles.value()));
        authorizationService.authorize(getUsername(request), roles);
    }

    private static String getUsername(final HttpServletRequest request) {
        final Object username = request.getAttribute(AUTHENTICATED_USERNAME);
        return username != null ? username.toString() : null;
    }

}
