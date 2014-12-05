package de.otto.hmac.authentication;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

class WrappedServletInputStream extends ServletInputStream {
    private final InputStream inputStream;

    public WrappedServletInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
