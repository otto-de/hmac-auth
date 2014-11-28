package de.otto.hmac.authentication.jersey.filter;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.client.impl.ClientRequestImpl;
import de.otto.hmac.HmacAttributes;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.joda.time.Instant;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.security.MessageDigest;

import static org.testng.Assert.assertEquals;

/**
 * Unit Test for {@link HMACJerseyClientFilter}.
 */
public class HMACJerseyClientFilterTest {

    @Test
    public void shouldCreateHMACSignatureOnClientRequest() throws Exception {
        ClientRequest cr = new ClientRequestImpl(new URI("/uri"), HttpMethod.POST);

        // test
        MessageDigest md5MessageDigest = RequestSigningUtil.getMD5Digest();
        md5MessageDigest.update(new String("abcd").getBytes());
        HMACJerseyClientFilter
                .addHmacHttpRequestHeaders(cr, "user", "secretKey", new Instant(123456789), md5MessageDigest);

        // assertions
        assertEquals(cr.getHeaders().get(HmacAttributes.X_HMAC_AUTH_SIGNATURE).get(0), "user:0xGKsmKRrbz6txscdugd3PBTpNKlVfAohDS4js9k4sQ=");
        assertEquals(cr.getHeaders().get(HmacAttributes.X_HMAC_AUTH_DATE).get(0), "1970-01-02T10:17:36.789Z");
    }
}
