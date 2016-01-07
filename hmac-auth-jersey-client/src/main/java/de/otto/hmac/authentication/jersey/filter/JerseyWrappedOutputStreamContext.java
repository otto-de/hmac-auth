package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import de.otto.hmac.authentication.WrappedOutputStreamContext;

public class JerseyWrappedOutputStreamContext implements WrappedOutputStreamContext {

    private final ClientRequest request;

    public JerseyWrappedOutputStreamContext(ClientRequest request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getRequestUri() {
        return request.getURI().getPath();
    }

    @Override
    public void putSingle(String header, String value) {
        request.getHeaders().putSingle(header, value);
    }
}
