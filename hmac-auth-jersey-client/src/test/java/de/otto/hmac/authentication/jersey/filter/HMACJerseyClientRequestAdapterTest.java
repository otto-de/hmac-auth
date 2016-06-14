package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import de.otto.hmac.authentication.WrappedOutputStream;
import org.testng.annotations.Test;

import java.io.OutputStream;
import java.time.Clock;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;

/**
 * Unit Test for {@link HMACJerseyClientRequestAdapter} class.
 */
public class HMACJerseyClientRequestAdapterTest {

    @Test
    public void shouldAdapt() throws Exception {
        // setup
        HMACJerseyClientRequestAdapter adapter = new HMACJerseyClientRequestAdapter("user", "secretKey", Clock.systemUTC());
        ClientRequest clientRequestMock = mock(ClientRequest.class);
        OutputStream outputStreamMock = mock(OutputStream.class);

        // test
        OutputStream result = adapter.adapt(clientRequestMock, outputStreamMock);

        assertTrue(result instanceof WrappedOutputStream);
    }
}
