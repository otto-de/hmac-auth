package de.otto.hmac.proxy;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.impl.ClientRequestImpl;
import org.testng.annotations.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static javax.ws.rs.core.UriBuilder.fromUri;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@Test
public class ProxyResourceTest {

    private static final HttpHeaders NO_HEADERS
            = null;

    @Test
    public void shouldAdjustTargetServerAndPort() {
        ProxyConfiguration.setPort(18);
        ProxyConfiguration.setTargetHost("SOME_HOST");

        UriBuilder builder = mock(UriBuilder.class);
        ProxyResource resource = new ProxyResource();
        resource.withTargetHostAndPort(builder);

        verify(builder, times(1)).host("SOME_HOST");
        verify(builder, times(1)).port(18);
    }

    @Test
    public void shouldUseCompletePathForRequest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getRequestUriBuilder()).thenReturn(fromUri("http://localhost:9998/p13n/menuItemConfigs/damenmode"));

        ProxyConfiguration.setPort(80);
        ProxyConfiguration.setUser("user");
        ProxyConfiguration.setPassword("secret");
        ProxyConfiguration.setTargetHost("develop.lhotse.ov.otto.de");

        ProxyResource proxyResource = new ProxyResource();
        WebResource.Builder target = proxyResource.createBuilder(uriInfo, "GET", NO_HEADERS);

        ClientRequestImpl clientRequest = getRequestFromWebresourceBuilder(target);

        assertThat(clientRequest.getURI().toString(), is("http://develop.lhotse.ov.otto.de:80/p13n/menuItemConfigs/damenmode"));
    }

    private ClientRequestImpl getRequestFromWebresourceBuilder(WebResource.Builder target) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method buildMethod = WebResource.Builder.class.getDeclaredMethod("build", String.class);
        buildMethod.setAccessible(true);
        return (ClientRequestImpl) buildMethod.invoke(target, "GET");
    }

}
