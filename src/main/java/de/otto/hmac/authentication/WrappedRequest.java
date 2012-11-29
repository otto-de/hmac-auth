package de.otto.hmac.authentication;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class WrappedRequest extends HttpServletRequestWrapper {

    private final String body;


    public static WrappedRequest wrap(HttpServletRequest request) throws IOException {
        if (request instanceof WrappedRequest) {
            return (WrappedRequest) request;
        }

        return new WrappedRequest(request);
    }

    private WrappedRequest(HttpServletRequest request) throws IOException {
        super(request);
        body = readBody(request);
    }

    private String readBody(ServletRequest request) throws IOException {

        final ServletInputStream inputStream = request.getInputStream();
        return inputStream != null ? IOUtils.toString(inputStream) : "";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream result = new ServletInputStream() {
            final ByteArrayInputStream inputStream = new ByteArrayInputStream(body.getBytes());

            public int read() throws IOException {
                return inputStream.read();
            }
        };
        return result;
    }

    public String getBody() {
        return body;
    }
}
