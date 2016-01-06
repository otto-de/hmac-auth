package de.otto.hmac.authentication.jersey2.filter;

import org.joda.time.Instant;

import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Outputstream wrapper to capture the content of the output stream. The HMAC signature is calculated on the content.
 * Internal class.
 */
class HmacJerseyOutputStreamWrapper extends OutputStream {

    private final String httpMethod;
    private final URI uri;
    private final Instant now;
    private final OutputStream out;
    private final ByteArrayOutputStream tmpOut;
    private final WriterInterceptorContext cr;
    private final String user;
    private final String secretKey;


    public HmacJerseyOutputStreamWrapper(final String httpMethod, final URI uri, final Instant now, final String user,
            final String secretKey, final WriterInterceptorContext cr, final OutputStream out) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.now = now;
        this.out = out;
        this.tmpOut = new ByteArrayOutputStream();
        this.cr = cr;
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        tmpOut.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        tmpOut.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        tmpOut.write(b);
    }

    @Override
    public void close() throws IOException {
        final byte[] bodyBytes = tmpOut.toByteArray();
        HmacJerseyHelper.addHmacHttpRequestHeaders(httpMethod, uri, user, secretKey, now, bodyBytes, cr.getHeaders());
        out.write(bodyBytes);
        tmpOut.close();
        out.flush();
        out.close();
    }
}
