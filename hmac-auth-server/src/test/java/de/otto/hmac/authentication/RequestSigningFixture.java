package de.otto.hmac.authentication;

import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;

public class RequestSigningFixture {


    public static String createSignature(WrappedRequest request, String user, String key) throws IOException {
        return createSignature(request.getRequestURI(), request.getMethod(), user, key, request.getHeader("x-hmac-auth-date"), request.getBody());
    }

    public static String createSignature(String uri, String method, String user, String key, String date, byte[] content) throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest(method, uri);
        request.addHeader("x-hmac-auth-date", date);
        request.setContent(content);
        return user + ":" + RequestSigningUtil.createRequestSignature(WrappedRequest.wrap(request), key);
    }

}
