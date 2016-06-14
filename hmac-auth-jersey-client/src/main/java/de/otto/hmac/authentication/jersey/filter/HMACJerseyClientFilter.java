package de.otto.hmac.authentication.jersey.filter;

import com.google.common.io.ByteSource;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import de.otto.hmac.authentication.WrappedOutputStream;

import javax.ws.rs.HttpMethod;
import java.time.Clock;
import java.time.Instant;

/**
 * The {@link de.otto.hmac.authentication.jersey.filter.HMACJerseyClientFilter} calculates HMAC signatures for jersey client
 * requests. Use this filter by simply adding it to your jersey client:
 * <code>
 *       try {
             client = Client.create(config);
             client.addFilter(new HMACJerseyClientFilter(hmacUser, hmacSecretKey));
         } catch (Exception e) {
             e.printStacktrace();
         }
 * </code>
 *
 * @author <a href="mailto:mathias.arens@googlemail.com">Mathias Arens</a>
 */
public class HMACJerseyClientFilter extends ClientFilter {

    private String user;
    private String secretKey;
    private final Clock clock;

    public HMACJerseyClientFilter(String user, String secretKey, Clock clock) {
        this.user = user;
        this.secretKey = secretKey;
        this.clock = clock;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (HttpMethod.POST.equalsIgnoreCase(cr.getMethod()) || HttpMethod.PUT.equalsIgnoreCase(cr.getMethod())) {
            cr.setAdapter(new HMACJerseyClientRequestAdapter(user, secretKey, clock));
        } else {
            addHmacHttpRequestHeaders(cr, user, secretKey, Instant.now(clock), ByteSource.empty());
        }
        return getNext().handle(cr);
    }

    public static void addHmacHttpRequestHeaders(final ClientRequest cr, final String user, final String secretKey, Instant now, ByteSource body) {
        WrappedOutputStream.addHmacHttpRequestHeaders(new JerseyWrappedOutputStreamContext(cr), user, secretKey, now, body);
    }
}
