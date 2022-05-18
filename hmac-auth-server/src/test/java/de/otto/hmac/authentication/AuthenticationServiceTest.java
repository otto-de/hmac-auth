package de.otto.hmac.authentication;

import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Instant;

import static de.otto.hmac.authentication.AuthenticationResult.Status.FAIL;
import static de.otto.hmac.authentication.AuthenticationResult.Status.SUCCESS;
import static de.otto.hmac.authentication.WrappedRequest.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;


@Test
public class AuthenticationServiceTest {

    @Test
    public void shouldAcceptValidRequest() throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", Instant.now().toString());

        request.setContent("{ \"key\": \"value\"}".getBytes());
        WrappedRequest wrappedRequest = wrap(request);
        String requestSignature = RequestSigningUtil.createRequestSignature(wrappedRequest, "secretKey");

        request.addHeader("x-hmac-auth-signature", "username:" + requestSignature);

        AuthenticationResult result = authService().validate(wrappedRequest);

        assertThat(result.getStatus(), is(SUCCESS));
        assertThat(result.getUsername(), is("username"));

    }

    @Test
    public void shouldRejectRequestIfUserUnknown() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", Instant.now().toString());

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

        AuthenticationService service = new AuthenticationService(userRepository, Clock.systemUTC());
        return service;
    }


}
