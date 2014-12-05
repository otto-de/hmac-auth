package de.otto.hmac.authentication;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper for a HttpServletRequest.
 *
 * The wrapper is needed to read the request body multiple times, for example during the authentication process
 * and later in the call stack to read the request body.
 */
public class WrappedRequest extends HttpServletRequestWrapper implements AutoCloseable {

    private FileBackedOutputStream body = new FileBackedOutputStream(10*1000*1000, true);

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
        return new WrappedServletInputStream(getBody().openStream());
    }

    public ByteSource getBody() {
        return body.asByteSource();
    }

    private WrappedRequest(final HttpServletRequest request) throws IOException {
        super(request);
        if (request.getInputStream() != null) {
            try (final InputStream inputStream = request.getInputStream()) {
                ByteStreams.copy(inputStream, body);
            }
        }
    }

    @Override
    public void close() throws IOException {
        body.close();
    }
}
