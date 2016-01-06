package de.otto.hmac.authentication.jersey2.filter;

import org.joda.time.Instant;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;
import java.net.URI;

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

    public static final String HTTP_METHOD = "httpMethod";
    public static final String HTTP_URI = "httpUri";
    public static final String NOW = "now";

    private String user;
    private String secretKey;

    public HmacJersey2ClientRequestFilter(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public void filter(final ClientRequestContext requestContext) throws IOException {
        final Instant now = new Instant();
        final String httpMethod = requestContext.getMethod();
        final URI uri = requestContext.getUri();
        requestContext.setProperty(HTTP_METHOD, httpMethod);
        requestContext.setProperty(HTTP_URI, uri);
        requestContext.setProperty(NOW, now);

        // set the HMAC request header attributes assuming that there is no entity to serialize
        // if there is a entity to serialize these attributes are overriden in interceptor
        HmacJerseyHelper.addHmacHttpRequestHeaders(httpMethod, uri, user, secretKey, now, null, requestContext.getHeaders());
    }
}
