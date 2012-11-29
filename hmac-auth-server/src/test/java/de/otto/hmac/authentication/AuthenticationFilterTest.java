package de.otto.hmac.authentication;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

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
        AuthenticationFilter filter = new AuthenticationFilter();

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

        AuthenticationFilter filter = new AuthenticationFilter();

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
        request.addHeader("x-p13n-signature", "username:AssumedToBeValid=");
        final String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());

        AuthenticationFilter filter = new AuthenticationFilter();
        AuthenticationService authService = mock(AuthenticationService.class);
        when(authService.validate((WrappedRequest) anyObject())).thenReturn(AuthenticationResult.success("username"));

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getKey(anyString())).thenReturn("secretKey");
        authService.setUserRepository(userRepository);

        FilterChainStub filterChain = new FilterChainStub();
        filter.setService(authService);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(filterChain.request.getAttribute("api-username").toString(), is("username"));

    }

    @Test
    public void shouldFailOnWrongSignature() throws Exception {

        //Given
        final MockHttpServletRequest request = putToSomeUriOnXmas();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("x-p13n-signature", "username:WrongSignature=");
        final String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());


        AuthenticationFilter filter = new AuthenticationFilter();
        AuthenticationService authService = new AuthenticationService();

        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.getKey(anyString())).thenReturn("secretKey");
        authService.setUserRepository(userRepository);

        FilterChainStub filterChain = new FilterChainStub();
        filter.setService(authService);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(filterChain.request, is(nullValue()));
        assertThat(response.getStatus(), is(401));

    }

    static String formattedDateOfXmas() {
        return new Instant(new DateTime(2012, 12, 24, 0, 0, 0, 0)).toString();
    }


    private MockHttpServletRequest putToSomeUriOnXmas() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-p13n-date", formattedDateOfXmas());
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
