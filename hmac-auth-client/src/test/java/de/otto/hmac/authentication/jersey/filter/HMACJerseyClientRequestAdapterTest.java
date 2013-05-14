package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import org.testng.annotations.Test;

import java.io.OutputStream;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * Unit Test for {@link HMACJerseyClientRequestAdapter} class.
 */
public class HMACJerseyClientRequestAdapterTest {

    @Test
    public void shouldAdapt() throws Exception {
        // setup
        HMACJerseyClientRequestAdapter adapter = new HMACJerseyClientRequestAdapter("user", "secretKey");
        ClientRequest clientRequestMock = mock(ClientRequest.class);
        OutputStream outputStreamMock = mock(OutputStream.class);

        // test
        OutputStream result = adapter.adapt(clientRequestMock, outputStreamMock);

        assertTrue(result instanceof HMACJerseyOutputStreamWrapper);
        assertEquals(((HMACJerseyOutputStreamWrapper) result).getUser(), "user");
        assertEquals(((HMACJerseyOutputStreamWrapper) result).getSecretKey(), "secretKey");
        assertSame(((HMACJerseyOutputStreamWrapper) result).getClientRequest(), clientRequestMock);
    }
}
