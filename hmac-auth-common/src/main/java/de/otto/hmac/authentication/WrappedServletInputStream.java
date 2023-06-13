package de.otto.hmac.authentication;

import jakarta.servlet.ServletInputStream;
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
    public int read(byte[] b, int off, int len) throws IOException {
        // using default InputStream.read method would blow
        // runtime as it falls back to single byte reading
        // in ServletInputStream. Be smarter, offer larger read chunk
        return inputStream.read(b, off, len);
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
