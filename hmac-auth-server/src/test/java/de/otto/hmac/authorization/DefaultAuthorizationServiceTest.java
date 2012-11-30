package de.otto.hmac.authorization;

import de.otto.hmac.FileSystemUserRepository;
import de.otto.hmac.HmacAttributes;
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


    @Test
    public void shouldAcceptNullInUnrestrictedGroup() throws Exception {
        authComponent(null).authorize(null, singleton("everybody"));
    }

    @Test
    public void shouldAcceptNullUserInSpecificGroup() throws Exception {
        authComponent(null).authorize(null, singleton("admin"));
    }

    @Test
    public void shouldAcceptNullUserEverywhere() {
        final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
        authComponent(null).authorize(null, roles);
    }

    @Test
    public void shouldNotAcceptEmptyUserInSpecificGroup() throws Exception {
        try  {
            authComponent("").authorize("", singleton("admin"));
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
        }
    }

    @Test
    public void shouldNotAcceptSomeUserInSpecificGroup() throws Exception {
        try  {
            authComponent("someUser").authorize("someUser", singleton("admin"));
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {

        }
    }

    @Test
    public void shouldAcceptExistingUserInSpecificGroup() throws Exception {
        authComponent("tom").authorize("tom", singleton("admin"));
    }


    @Test
    public void shouldAcceptEmptyInUnrestrictedGroup() throws Exception {
        authComponent("").authorize("", singleton("everybody"));
    }

    @Test
    public void shouldAcceptSomeStringInUnrestrictedGroup() throws Exception {
        authComponent("someUser").authorize("someUser", singleton("everybody"));
    }

    private DefaultAuthorizationService authComponent(String someUser) {
        FileSystemUserRepository apiUserRepository = mock(FileSystemUserRepository.class);
        when(apiUserRepository.getRolesForUser(eq("tom"))).thenReturn(singleton("admin"));
        when(apiUserRepository.getRolesForUser(eq("someUser"))).thenReturn(singleton("everybody"));
        when(apiUserRepository.getRolesForUser(eq(""))).thenReturn(singleton("everybody"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(AUTHENTICATED_USERNAME)).thenReturn(someUser);

        DefaultAuthorizationService defaultAuthorizationComponent = new DefaultAuthorizationService();
        defaultAuthorizationComponent.setUserRepository(apiUserRepository);
        return defaultAuthorizationComponent;
    }

    @Test
    public void shouldGiveValuableErrorMessageWithEmptyUser() {
        try  {
            final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
            authComponent("").authorize("", roles);
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("Anonymous user is not in one of these groups: [admin, shopoffice]."));
        }
    }

    @Test
    public void shouldGiveValuableErrorMessageWithNamesUser() {
        try  {
            final Set<String> roles = new HashSet<>(asList("admin", "shopoffice"));
            authComponent("someUnauthorizedUser").authorize("someUnauthorizedUser", roles);
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("[someUnauthorizedUser] is not in one of these groups: [admin, shopoffice]."));
        }
    }

}
