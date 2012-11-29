package de.otto.hmac.authorization;

import java.lang.annotation.*;


/**
 * Die Annotation wird vom RolesAuthorizationAspect ausgewertet
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AllowedForRoles {

    String[] value();

}
