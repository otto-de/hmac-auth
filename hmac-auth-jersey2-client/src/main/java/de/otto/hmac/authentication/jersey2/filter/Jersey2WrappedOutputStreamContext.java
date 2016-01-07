package de.otto.hmac.authentication.jersey2.filter;

import de.otto.hmac.authentication.WrappedOutputStreamContext;

import javax.ws.rs.client.ClientRequestContext;

class Jersey2WrappedOutputStreamContext implements WrappedOutputStreamContext {
    private final ClientRequestContext requestContext;

    public Jersey2WrappedOutputStreamContext(ClientRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public String getMethod() {
        return requestContext.getMethod();
    }

    @Override
    public String getRequestUri() {
        return requestContext.getUri().getPath();
    }

    @Override
    public void putSingle(String header, String value) {
        requestContext.getHeaders().putSingle(header, value);
    }
}
