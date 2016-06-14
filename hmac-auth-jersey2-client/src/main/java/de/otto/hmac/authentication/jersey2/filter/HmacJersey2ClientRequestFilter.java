package de.otto.hmac.authentication.jersey2.filter;

import com.google.common.io.ByteSource;
import de.otto.hmac.authentication.WrappedOutputStream;
import de.otto.hmac.authentication.WrappedOutputStreamContext;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * Jersey2 ClientRequestFilter for HMAC request signatures. Register a HMAC client request filter and writer interceptor combination
 * with the HmacJerseyHelper for example:
 *
 * <code>
 * Client client = ClientBuilder.newBuilder().build();
 * HmacJerseyHelper.registerHmacFilter(client, "user", "hmacSecret");
 * </code>
 *
 * @author <a href="mailto:mathias.arens@googlemail.com">Mathias Arens</a>
 */
public class HmacJersey2ClientRequestFilter implements ClientRequestFilter {

    private String user;
    private String secretKey;
    private final Clock clock;

    public HmacJersey2ClientRequestFilter(final String user, final String secretKey, final Clock clock) {
        this.user = user;
        this.secretKey = secretKey;
        this.clock = clock;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        WrappedOutputStreamContext wrappedOutputStreamContext = new Jersey2WrappedOutputStreamContext(requestContext);
        if(requestContext.hasEntity()) {
            requestContext.setEntityStream(new WrappedOutputStream(
                    user,
                    secretKey,
                    wrappedOutputStreamContext,
                    requestContext.getEntityStream(),
                    clock));
        } else {
            WrappedOutputStream.addHmacHttpRequestHeaders(
                    wrappedOutputStreamContext,
                    user,
                    secretKey,
                    Instant.now(clock),
                    ByteSource.empty());
        }
    }

}
