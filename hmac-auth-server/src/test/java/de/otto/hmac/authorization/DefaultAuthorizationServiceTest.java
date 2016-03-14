package de.otto.hmac.authorization;

import de.otto.hmac.FileSystemUserRepository;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

import static de.otto.hmac.HmacAttributes.AUTHENTICATED_USERNAME;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

@Test
public class DefaultAuthorizationServiceTest {

    private static class ConfigWithAuthSet implements HmacConfiguration {
        private boolean disableAuthorizationForUnsignedRequests;

        private ConfigWithAuthSet(boolean disableAuthorizationForUnsignedRequests) {
            this.disableAuthorizationForUnsignedRequests = disableAuthorizationForUnsignedRequests;
        }

        public static ConfigWithAuthSet configWithoutAuth() {
            return new ConfigWithAuthSet(true);
        }

        public static ConfigWithAuthSet configWithAuth() {
            return new ConfigWithAuthSet(false);
        }

        @Override
        public boolean disableAuthorizationForUnsignedRequests() {
            return disableAuthorizationForUnsignedRequests;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    @Test
    public void shouldAcceptNullInUnrestrictedGroupWithDisabledAuth() throws Exception {
        DefaultAuthorizationService service = authComponent(null, ConfigWithAuthSet.configWithoutAuth());
        service.authorize(null, singleton("everybody"));
    }

    @Test
    public void shouldAcceptNullUserInSpecificGroupWithDisabledAuth() throws Exception {
        DefaultAuthorizationService service = authComponent(null, ConfigWithAuthSet.configWithoutAuth());
        service.authorize(null, singleton("admin"));
    }

    @Test
    public void shouldAcceptNullUserEverywhereWithDisabledAuth() {
        final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
        DefaultAuthorizationService service = authComponent(null, ConfigWithAuthSet.configWithoutAuth());
        service.authorize(null, roles);
    }

    @Test
    public void shouldNotAcceptNullUserInSpecificGroupWithEnabledAuth() throws Exception {
        try {
            DefaultAuthorizationService service = authComponent(null, ConfigWithAuthSet.configWithAuth());
            service.authorize(null, singleton("admin"));
            fail("Should not authorize null user");
        } catch (AuthorizationException e) {
        }
    }

    @Test
    public void shouldNotAcceptNullUserEverywhereWithEnabledAuth() {
        final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));

        try {
            DefaultAuthorizationService service = authComponent(null, ConfigWithAuthSet.configWithAuth());
            service.authorize(null, roles);
            fail("Should not authorize null user");
        } catch (AuthorizationException e) {
        }
    }


    @Test
    public void shouldNotAcceptEmptyUserInSpecificGroup() throws Exception {
        try {
            authComponent("", ConfigWithAuthSet.configWithAuth()).authorize("", singleton("admin"));
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
        }
    }

    @Test
    public void shouldNotAcceptSomeUserInSpecificGroup() throws Exception {
        try {
            authComponent("someUser", ConfigWithAuthSet.configWithAuth()).authorize("someUser", singleton("admin"));
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {

        }
    }

    @Test
    public void shouldAcceptExistingUserInSpecificGroup() throws Exception {
        authComponent("tom", ConfigWithAuthSet.configWithAuth()).authorize("tom", singleton("admin"));
    }


    @Test
    public void shouldAcceptEmptyInUnrestrictedGroup() throws Exception {
        authComponent("", ConfigWithAuthSet.configWithAuth()).authorize("", singleton("everybody"));
    }

    @Test
    public void shouldAcceptSomeStringInUnrestrictedGroup() throws Exception {
        authComponent("someUser", ConfigWithAuthSet.configWithAuth()).authorize("someUser", singleton("everybody"));
    }

    private DefaultAuthorizationService authComponent(String someUser, HmacConfiguration hmacConfiguration) {
        FileSystemUserRepository apiUserRepository = mock(FileSystemUserRepository.class);
        when(apiUserRepository.getRolesForUser(eq("tom"))).thenReturn(singleton("admin"));
        when(apiUserRepository.getRolesForUser(eq("someUser"))).thenReturn(singleton("everybody"));
        when(apiUserRepository.getRolesForUser(eq(""))).thenReturn(singleton("everybody"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(AUTHENTICATED_USERNAME)).thenReturn(someUser);

        DefaultAuthorizationService defaultAuthorizationComponent = new DefaultAuthorizationService(apiUserRepository, hmacConfiguration);
        return defaultAuthorizationComponent;
    }

    @Test
    public void shouldGiveValuableErrorMessageWithEmptyUser() {
        try {
            final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
            authComponent("", ConfigWithAuthSet.configWithAuth()).authorize("", roles);
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("[Anonymous user] is not in one of the required security groups."));
        }
    }

    @Test
    public void shouldGiveValuableErrorMessageWithNamesUser() {
        try {
            final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
            authComponent("someUnauthorizedUser", ConfigWithAuthSet.configWithAuth()).authorize("someUnauthorizedUser", roles);
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("[someUnauthorizedUser] is not in one of the required security groups."));
        }
    }

}
