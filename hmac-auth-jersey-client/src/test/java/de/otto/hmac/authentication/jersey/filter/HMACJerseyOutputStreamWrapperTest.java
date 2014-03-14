package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.core.util.StringKeyObjectValueIgnoreCaseMultivaluedMap;
import de.otto.hmac.HmacAttributes;
import org.testng.annotations.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Unit Test for {@link HMACJerseyOutputStreamWrapper} class.
 */
public class HMACJerseyOutputStreamWrapperTest {

    @Test
    public void shouldNotWriteOutputStreamOnWriteMethods() throws IOException {
        // setup
        ClientRequest clientRequestMock = mock(ClientRequest.class);
        OutputStream outputStreamMock = mock(OutputStream.class);
        HMACJerseyOutputStreamWrapper wrapper =
                new HMACJerseyOutputStreamWrapper("user", "secretKey", clientRequestMock, outputStreamMock);

        // test
        wrapper.write(new String("br").getBytes(), 0, 2);
        wrapper.write(new String("br").getBytes());
        wrapper.write((byte) 10);

        verify(outputStreamMock, never()).write((byte[]) any(), anyInt(), anyInt());
        verify(outputStreamMock, never()).write((byte[]) any());
        verify(outputStreamMock, never()).write(anyByte());
    }

    @Test
    public void shouldWriteOutputStreamOnFlush() throws IOException, URISyntaxException {
        // setup
        MultivaluedMap<String, Object> map = new StringKeyObjectValueIgnoreCaseMultivaluedMap();
        ClientRequest clientRequestMock = mock(ClientRequest.class);
        when(clientRequestMock.getMethod()).thenReturn("GET");
        when(clientRequestMock.getURI()).thenReturn(new URI("http://localhost:8080/test"));
        when(clientRequestMock.getHeaders()).thenReturn(map);
        OutputStream outputStreamMock = mock(OutputStream.class);

        HMACJerseyOutputStreamWrapper wrapper =
                new HMACJerseyOutputStreamWrapper("user", "secretKey", clientRequestMock, outputStreamMock);

        // test
        wrapper.write(new String("br").getBytes(), 0, 2);
        wrapper.write(new String("br").getBytes());
        wrapper.write((byte) 10);
        wrapper.close();

        verify(outputStreamMock).write(new byte[] {(byte) 'b', (byte) 'r', (byte) 'b', (byte) 'r', (byte) 10});
        assertNotNull(map.get(HmacAttributes.X_HMAC_AUTH_SIGNATURE));
        assertNotNull(map.get(HmacAttributes.X_HMAC_AUTH_DATE));
    }
}
