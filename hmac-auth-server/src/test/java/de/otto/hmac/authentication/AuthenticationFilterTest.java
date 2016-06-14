package de.otto.hmac.authentication;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;

@Test
public class AuthenticationFilterTest {

    @Test
    public void shouldHandThroughUnsignedRequest() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        AuthenticationFilter filter = new AuthenticationFilter(null);

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void shouldWrapSignedRequest() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        AuthenticationFilter filter = new AuthenticationFilter(null);

        // When
        filter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(any(WrappedRequest.class), any(MockHttpServletResponse.class));
    }

    @Test
    public void shouldAddUserNameToRequest() throws Exception {

        //Given
        final MockHttpServletRequest request = putToSomeUriOnXmas();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-hmac-auth-signature", "username:AssumedToBeValid=");
        final String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());

        AuthenticationService authService = mock(AuthenticationService.class);
        when(authService.validate((WrappedRequest) anyObject())).thenReturn(AuthenticationResult.success("username"));
        AuthenticationFilter filter = new AuthenticationFilter(authService);

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getKey(anyString())).thenReturn("secretKey");

        FilterChainStub filterChain = new FilterChainStub();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(filterChain.request.getAttribute("authenticated-username").toString(), is("username"));

    }

    @Test
    public void shouldFailOnWrongSignature() throws Exception {

        //Given
        final MockHttpServletRequest request = putToSomeUriOnXmas();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-hmac-auth-signature", "username:WrongSignature=");
        final String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getKey(anyString())).thenReturn("secretKey");

        AuthenticationService authService = new AuthenticationService(userRepository);
        AuthenticationFilter filter = new AuthenticationFilter(authService);

        FilterChainStub filterChain = new FilterChainStub();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(filterChain.request, is(nullValue()));
        assertThat(response.getStatus(), is(401));

    }

    static String formattedDateOfXmas() {
        return LocalDateTime.of(2012,12,24,0,0,0,0).toInstant(ZoneOffset.UTC).toString();
    }


    private MockHttpServletRequest putToSomeUriOnXmas() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfXmas());
        return request;
    }

    private class FilterChainStub implements FilterChain {
        private ServletRequest request;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            this.request = request;
        }
    }
}
