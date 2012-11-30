package de.otto.hmac.authorization;

import de.otto.hmac.HmacAttributes;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.HmacAttributes.AUTHENTICATED_USERNAME;
import static java.util.Arrays.asList;

@Aspect
@Component
public class RolesAuthorizationAspect {

    private AuthorizationService authorizationService;
    private HttpServletRequest request;

    @Resource
    @Required
    public void setRequest(final HttpServletRequest request) {
        this.request = request;
    }

    @Resource
    @Required
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Before("@target(org.springframework.stereotype.Controller) && @annotation(allowedForRoles)")
    public void assertAuthorized(JoinPoint jp, AllowedForRoles allowedForRoles) {
        final Set<String> roles = new HashSet<>(asList(allowedForRoles.value()));
        authorizationService.authorize(getUsername(request), roles);
    }

    private static String getUsername(final HttpServletRequest request) {
        final Object username = request.getAttribute(AUTHENTICATED_USERNAME);
        return username!=null ? username.toString() : null;
    }

}
