package de.otto.hmac.authentication;

public interface WrappedOutputStreamContext {

    String getMethod();

    String getRequestUri();

    void putSingle(String header, String value);
}
