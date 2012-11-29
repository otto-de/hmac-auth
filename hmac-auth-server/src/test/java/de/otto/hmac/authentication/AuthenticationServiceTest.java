package de.otto.hmac.authentication;

import org.joda.time.Instant;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import static de.otto.hmac.authentication.AuthenticationResult.Status.FAIL;
import static de.otto.hmac.authentication.AuthenticationResult.Status.SUCCESS;
import static de.otto.hmac.authentication.WrappedRequest.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.eq;

@Test
public class AuthenticationServiceTest {

    @Test
    public void shouldAcceptValidRequest() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", new Instant().toString());

        request.setContent("{ \"key\": \"value\"}".getBytes());
        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");

        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);


        AuthenticationResult result = authService().validate(wrap(request));

        assertThat(result.getStatus(), is(SUCCESS));
        assertThat(result.getUsername(), is("username"));

    }

    @Test
    public void shouldRejectRequestIfUserUnknown() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", new Instant().toString());

        String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());

        String signature = RequestSigningFixture.createSignature(wrap(request), "unknownUser", "secretKey");
        request.addHeader("x-hmac-auth-signature", signature);

        AuthenticationResult result = authService().validate(wrap(request));

        assertThat(result.getStatus(), is(FAIL));
        assertThat(result.getUsername(), is(nullValue()));

    }

    private AuthenticationService authService() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.getKey(eq("username"))).thenReturn("secretKey");

        AuthenticationService service = new AuthenticationService();
        service.setUserRepository(userRepository);
        return service;
    }


}
