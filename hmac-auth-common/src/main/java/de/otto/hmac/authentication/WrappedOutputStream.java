package de.otto.hmac.authentication;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.FileBackedOutputStream;
import de.otto.hmac.HmacAttributes;
import org.joda.time.Instant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WrappedOutputStream extends OutputStream {

    private final OutputStream out;
    private final FileBackedOutputStream tmpOut;
    private final WrappedOutputStreamContext cr;
    private final String user;
    private final String secretKey;

    public WrappedOutputStream(final String user, final String secretKey, final WrappedOutputStreamContext cr,
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
        addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), tmpOut.asByteSource());

        if (tmpOut.asByteSource().isEmpty()) {
            // workaround for bug in jersey: without writing a single byte to the
            // underlying CommittingOutputStream, its method commit/commitStream will not be executed.
            // This is responsible to write lately added headers either via
            // abstract method getOutputStream() (jersey1) or
            // OutboundMessageContext.StreamProvider.getOutputStream()
            //
            // this call enforces a commit call and does nothing else
            out.write("".getBytes(), 0 , 0);
        }
        try (InputStream in = tmpOut.asByteSource().openBufferedStream()) {
            ByteStreams.copy(in, out);
        }

        tmpOut.close();
        out.close();
    }

    public static void addHmacHttpRequestHeaders(
            final WrappedOutputStreamContext cr,
            final String user, final
            String secretKey,
            Instant now,
            ByteSource body) {
        String signatureHeader = user + ":" + RequestSigningUtil.createRequestSignature(cr.getMethod(), now.toString(),
                cr.getRequestUri(), body, secretKey);
        cr.putSingle(HmacAttributes.X_HMAC_AUTH_SIGNATURE, signatureHeader);
        cr.putSingle(HmacAttributes.X_HMAC_AUTH_DATE, now.toString());
    }

}
