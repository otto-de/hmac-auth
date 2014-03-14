package de.otto.hmac.authentication.jersey2.filter;

import de.otto.hmac.HmacAttributes;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.apache.commons.codec.Charsets;
import org.joda.time.Instant;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;

/**
 * Created by marens on 28.02.14.
 */
public class HmacJerseyHelper {

    public static void addHmacHttpRequestHeaders(final String httpMethod, final URI uri, final String user, final String secretKey,
            final Instant now, final byte[] body, final MultivaluedMap<String, Object> headers) {
        String signatureHeader = user + ":" + RequestSigningUtil.createRequestSignature(httpMethod, now.toString(),
                uri.getPath(), body != null ? new String(body, Charsets.UTF_8) : "", secretKey);
        headers.putSingle(HmacAttributes.X_HMAC_AUTH_SIGNATURE, signatureHeader);
        headers.putSingle(HmacAttributes.X_HMAC_AUTH_DATE, now.toString());
    }

    public static Client registerHmacFilter(final Client client, final String user, final String secretKey) {
        client.register(new HmacJersey2ClientRequestFilter(user, secretKey));
        client.register(new HmacJersey2WriterInterceptor(user, secretKey));
        return client;
    }
}
