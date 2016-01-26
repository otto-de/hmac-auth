package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientRequestAdapter;
import de.otto.hmac.authentication.WrappedOutputStream;
import de.otto.hmac.authentication.WrappedOutputStreamContext;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Register {@link WrappedOutputStream}. Internal class.
 * @see HMACJerseyClientFilter
 */
class HMACJerseyClientRequestAdapter implements ClientRequestAdapter {

    private String user;
    private String secretKey;

    public HMACJerseyClientRequestAdapter(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public OutputStream adapt(final ClientRequest request, final OutputStream out) throws IOException {
        return new WrappedOutputStream(user, secretKey, new JerseyWrappedOutputStreamContext(request), out);
    }
}
