package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import de.otto.hmac.HmacAttributes;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.joda.time.Instant;

import javax.ws.rs.HttpMethod;
import java.security.MessageDigest;

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
            addHmacHttpRequestHeaders(cr, user, secretKey, new Instant(), RequestSigningUtil.getMD5Digest());
        }
        return getNext().handle(cr);
    }

    public static void addHmacHttpRequestHeaders(final ClientRequest cr, final String user, final String secretKey, Instant now, MessageDigest md5MessageDigest) {
        String signatureHeader = user + ":" + RequestSigningUtil.createRequestSignature(cr.getMethod(), now.toString(),
                cr.getURI().getPath(), md5MessageDigest, secretKey);
        cr.getHeaders().putSingle(HmacAttributes.X_HMAC_AUTH_SIGNATURE, signatureHeader);
        cr.getHeaders().putSingle(HmacAttributes.X_HMAC_AUTH_DATE, now.toString());
    }
}
