package de.otto.hmac;

import de.otto.hmac.authentication.AuthenticationFilter;
import de.otto.hmac.authentication.AuthenticationService;
import de.otto.hmac.authorization.DefaultAuthorizationService;
import de.otto.hmac.authorization.RolesAuthorizationAspect;
import de.otto.hmac.repository.FileSystemUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;

@Configuration
public class SpringConfiguration {

    @Value(value = "${hmac.auth.xml}")
    private String authXmlResource;

    @Autowired
    private HttpServletRequest request;

    @Bean
    public FileSystemUserRepository userRepository() {
        return new FileSystemUserRepository(authXmlResource);
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(userRepository(), Clock.systemUTC());
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(authenticationService());
    }

    @Bean
    public DefaultHmacConfiguration defaultHmacConfiguration() {
        return new DefaultHmacConfiguration();
    }

    @Bean
    public DefaultAuthorizationService defaultAuthorizationService() {
        return new DefaultAuthorizationService(userRepository(), defaultHmacConfiguration());
    }

    @Bean
    public RolesAuthorizationAspect rolesAuthorizationAspect() {
        return new RolesAuthorizationAspect(defaultAuthorizationService(), request);
    }
}
