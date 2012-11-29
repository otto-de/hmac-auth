package de.otto.hmac.authorization;

import de.otto.hmac.FileSystemUserRepository;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;

import static de.otto.hmac.authentication.AuthenticationFilter.API_USERNAME;
import static java.util.Collections.singleton;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.testng.Assert.fail;

@Test
public class DefaultAuthorizationServiceTest {


    @Test
    public void shouldAcceptNullInUnrestrictedGroup() throws Exception {
        authComponent(null).authorize("everybody");
    }

    @Test
    public void shouldAcceptNullUserInSpecificGroup() throws Exception {
        authComponent(null).authorize("admin");
    }

    @Test
    public void shouldAcceptNullUserEverywhere() {
        authComponent(null).authorize("admin", "shopoffice");
    }

    @Test
    public void shouldNotAcceptEmptyUserInSpecificGroup() throws Exception {
        try  {
            authComponent("").authorize("admin");
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
        }
    }

    @Test
    public void shouldNotAcceptSomeUserInSpecificGroup() throws Exception {
        try  {
            authComponent("someUser").authorize("admin");
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {

        }
    }

    @Test
    public void shouldAcceptExistingUserInSpecificGroup() throws Exception {
        authComponent("tom").authorize("admin");
    }


    @Test
    public void shouldAcceptEmptyInUnrestrictedGroup() throws Exception {
        authComponent("").authorize("everybody");
    }

    @Test
    public void shouldAcceptSomeStringInUnrestrictedGroup() throws Exception {
        authComponent("someUser").authorize("everybody");
    }

    private DefaultAuthorizationService authComponent(String someUser) {
        FileSystemUserRepository apiUserRepository = mock(FileSystemUserRepository.class);
        when(apiUserRepository.getRolesForUser(eq("tom"))).thenReturn(singleton("admin"));
        when(apiUserRepository.getRolesForUser(eq("someUser"))).thenReturn(singleton("everybody"));
        when(apiUserRepository.getRolesForUser(eq(""))).thenReturn(singleton("everybody"));

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute(API_USERNAME)).thenReturn(someUser);

        DefaultAuthorizationService defaultAuthorizationComponent = new DefaultAuthorizationService();
        defaultAuthorizationComponent.setUserRepository(apiUserRepository);
        defaultAuthorizationComponent.setRequest(request);
        return defaultAuthorizationComponent;
    }

    @Test
    public void shouldGiveValuableErrorMessageWithEmptyUser() {
        try  {
            authComponent("").authorize("admin", "shopoffice");
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("Anonymous user is not in one of these groups: [admin, shopoffice]."));
        }
    }

    @Test
    public void shouldGiveValuableErrorMessageWithNamesUser() {
        try  {
            authComponent("someUnauthorizedUser").authorize("admin", "shopoffice");
            fail("Should not authorize if user is not in Group");
        } catch (AuthorizationException e) {
            assertThat(e.getMessage(), is("[someUnauthorizedUser] is not in one of these groups: [admin, shopoffice]."));
        }
    }

}
