package de.otto.hmac.authentication;

import de.otto.hmac.StringUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * A wrapper for a HttpServletRequest.
 * <p/>
 * The wrapper is needed to read the request body multiple times, for example during the authentication process
 * and later in the call stack to read the request body.
 *
 */
public class WrappedRequest extends HttpServletRequestWrapper {

    private final String body;

    /**
     * Factory method used to create a WrappedRequest, wrapping a HttpServletRequest.
     *
     * @param request the HttServletRequest
     * @return WrappedRequest
     * @throws IOException if reading the request body fails.
     */
    public static WrappedRequest wrap(final HttpServletRequest request) throws IOException {
        if (request instanceof WrappedRequest) {
            return (WrappedRequest) request;
        }

        return new WrappedRequest(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes());

            public int read() throws IOException {
                return inputStream.read();
            }
        };
    }

    public String getBody() {
        return body;
    }

    private WrappedRequest(final HttpServletRequest request) throws IOException {
        super(request);
        String result;
        try (final ServletInputStream inputStream = request.getInputStream()) {
            result = inputStream != null ? StringUtils.toString(inputStream) : "";
        }
        body = result;
    }

}
