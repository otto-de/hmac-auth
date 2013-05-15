package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import de.otto.hmac.HmacAttributes;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.apache.commons.codec.Charsets;
import org.joda.time.Instant;

import javax.ws.rs.HttpMethod;

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

    public HMACJerseyClientFilter(String user, String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public ClientResponse handle(ClientRequest cr) throws ClientHandlerException {
        if (HttpMethod.POST.equalsIgnoreCase(cr.getMethod()) || HttpMethod.PUT.equalsIgnoreCase(cr.getMethod())) {
            cr.setAdapter(new HMACJerseyClientRequestAdapter(user, secretKey));
        } else {
            addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), null);
        }
        return getNext().handle(cr);
    }

    public static void addHmacHttpRequestHeaders(final ClientRequest cr, final String user, final String secretKey,
            Instant now, final byte[] body) {
        String signatureHeader = user + ":" + RequestSigningUtil.createRequestSignature(cr.getMethod(), now.toString(),
                cr.getURI().getPath(), body != null ? new String(body, Charsets.UTF_8) : "", secretKey);
        cr.getHeaders().add(HmacAttributes.X_HMAC_AUTH_SIGNATURE, signatureHeader);
        cr.getHeaders().add(HmacAttributes.X_HMAC_AUTH_DATE, now.toString());
    }
}
