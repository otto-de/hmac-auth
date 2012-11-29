package de.otto.hmac.authorization;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
public class RolesAuthorizationAspect {

    private AuthorizationService authorizationService;

    @Resource
    @Required
    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Before("@target(org.springframework.stereotype.Controller) && @annotation(allowedForRoles)")
    public void assertAuthorized(JoinPoint jp, AllowedForRoles allowedForRoles) {
        authorizationService.authorize(allowedForRoles.value());
    }
}
