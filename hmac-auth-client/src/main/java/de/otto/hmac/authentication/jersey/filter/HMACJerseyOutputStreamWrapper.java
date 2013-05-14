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
 * @see HMACJerseyClientFilter
 */
class HMACJerseyOutputStreamWrapper extends OutputStream {

    private OutputStream out;
    private ByteArrayOutputStream tmpOut;
    private ClientRequest cr;
    private String user;
    private String secretKey;


    public HMACJerseyOutputStreamWrapper(final String user, final String secretKey, final ClientRequest cr,
            final OutputStream out) {
        this.out = out;
        tmpOut = new ByteArrayOutputStream();
        this.cr = cr;
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        tmpOut.write(b, off, len);
        out.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        tmpOut.write(b);
        out.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        tmpOut.write(b);
        out.write(b);
    }

    @Override
    public void flush() throws IOException {
        tmpOut.flush();
        HMACJerseyClientFilter.addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), tmpOut.toByteArray());
        out.flush();
    }

    @Override
    public void close() throws IOException {
        tmpOut.close();
        HMACJerseyClientFilter.addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), tmpOut.toByteArray());
        out.close();

    }
}
