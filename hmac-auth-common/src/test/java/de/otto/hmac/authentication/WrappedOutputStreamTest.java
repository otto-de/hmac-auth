package de.otto.hmac.authentication;

import de.otto.hmac.HmacAttributes;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.time.Clock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class WrappedOutputStreamTest {

    @Test
    public void shouldNotWriteOutputStreamOnWriteMethods() throws IOException {
        // setup
        WrappedOutputStreamContext clientRequestMock = mock(WrappedOutputStreamContext.class);
        OutputStream outputStreamMock = mock(OutputStream.class);
        WrappedOutputStream wrapper =
                new WrappedOutputStream("user", "secretKey", clientRequestMock, outputStreamMock, Clock.systemUTC());

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
        WrappedOutputStreamContext clientRequestMock = mock(WrappedOutputStreamContext.class);
        when(clientRequestMock.getMethod()).thenReturn("GET");
        when(clientRequestMock.getRequestUri()).thenReturn("/test");

        ByteArrayOutputStream outputStreamMock = new ByteArrayOutputStream();

        WrappedOutputStream wrapper =
                new WrappedOutputStream("user", "secretKey", clientRequestMock, outputStreamMock, Clock.systemUTC());

        // test
        wrapper.write(new String("br").getBytes(), 0, 2);
        wrapper.write(new String("br").getBytes());
        wrapper.write((byte) 10);
        wrapper.close();

        assertEquals(new byte[] {(byte) 'b', (byte) 'r', (byte) 'b', (byte) 'r', (byte) 10}, outputStreamMock.toByteArray());

        verify(clientRequestMock).putSingle(eq(HmacAttributes.X_HMAC_AUTH_SIGNATURE), anyString());
        verify(clientRequestMock).putSingle(eq(HmacAttributes.X_HMAC_AUTH_DATE), anyString());
    }

}