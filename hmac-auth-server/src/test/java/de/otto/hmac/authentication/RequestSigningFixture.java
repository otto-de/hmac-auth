package de.otto.hmac.authentication;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestSigningFixture {


    public static String createSignature(WrappedRequest request, String user, String key) throws IOException {
        return createSignature(request.getRequestURI(), request.getMethod(), user, key, request.getHeader("x-hmac-auth-date"), request.getBody());
    }

    public static String createSignature(String uri, String method, String user, String key, String date, ByteSource body) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.addHeader("x-hmac-auth-date", date);
        request.setContent(extractContent(body));
        return user + ":" + RequestSigningUtil.createRequestSignature(WrappedRequest.wrap(request), key);
    }

    private static byte[] extractContent(ByteSource body) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try(InputStream inputStream = body.openBufferedStream()) {
            ByteStreams.copy(inputStream, out);
        }
        return out.toByteArray();
    }

}
