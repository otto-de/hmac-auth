package de.otto.hmac.authentication.jersey.filter;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import com.sun.jersey.api.client.ClientRequest;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.joda.time.Instant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

/**
 * Outputstream wrapper to capture the content of the output stream. The HMAC signature is calculated on the content.
 * Internal class.
 *
 * @see de.otto.hmac.authentication.jersey.filter.HMACJerseyClientFilter
 */
class HMACJerseyOutputStreamWrapper extends OutputStream {

    private final OutputStream out;
    private final FileBackedOutputStream tmpOut;
    private final ClientRequest cr;
    private final String user;
    private final String secretKey;

    public HMACJerseyOutputStreamWrapper(final String user, final String secretKey, final ClientRequest cr,
            final OutputStream out) {
        this.out = out;
        tmpOut = new FileBackedOutputStream(10 * 1000 * 1000);
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

        HMACJerseyClientFilter.addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), tmpOut.asByteSource());

        try(InputStream in = tmpOut.asByteSource().openBufferedStream()) {
            ByteStreams.copy(in, out);
        }
        out.flush();
        out.close();
        tmpOut.close();
    }
}
