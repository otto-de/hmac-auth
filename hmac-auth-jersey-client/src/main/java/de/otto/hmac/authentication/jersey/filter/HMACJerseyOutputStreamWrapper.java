package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import org.joda.time.Instant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Outputstream wrapper to capture the content of the output stream. The HMAC signature is calculated on the content.
 * Internal class.
 *
 * @see de.otto.hmac.authentication.jersey.filter.HMACJerseyClientFilter
 */
class HMACJerseyOutputStreamWrapper extends OutputStream {

    private final OutputStream out;
    private final ByteArrayOutputStream tmpOut;
    private final ClientRequest cr;
    private final String user;
    private final String secretKey;


    public HMACJerseyOutputStreamWrapper(final String user, final String secretKey, final ClientRequest cr,
            final OutputStream out) {
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
        final byte [] bodyBytes = tmpOut.toByteArray();
        HMACJerseyClientFilter.addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), bodyBytes);
        out.write(bodyBytes);
        out.flush();
        out.close();
        tmpOut.close();
    }
}
