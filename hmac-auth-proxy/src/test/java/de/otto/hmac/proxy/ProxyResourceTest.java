package de.otto.hmac.proxy;

import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Test
public class ProxyResourceTest {
    
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


}
