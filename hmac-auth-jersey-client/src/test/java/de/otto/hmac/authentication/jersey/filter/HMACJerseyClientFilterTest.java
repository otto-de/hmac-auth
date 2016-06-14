package de.otto.hmac.authentication.jersey.filter;

import com.google.common.io.ByteSource;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.client.impl.ClientRequestImpl;
import de.otto.hmac.HmacAttributes;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;
import java.net.URI;
import java.time.Instant;

import static org.testng.Assert.assertEquals;

/**
 * Unit Test for {@link HMACJerseyClientFilter}.
 */
public class HMACJerseyClientFilterTest {

    @Test
    public void shouldCreateHMACSignatureOnClientRequest() throws Exception {
        ClientRequest cr = new ClientRequestImpl(new URI("/uri"), HttpMethod.POST);

        // test
        ByteSource body = ByteSource.wrap("abcd".getBytes());
        HMACJerseyClientFilter
                .addHmacHttpRequestHeaders(cr, "user", "secretKey", Instant.ofEpochMilli(123456789), body);

        // assertions
        assertEquals(cr.getHeaders().get(HmacAttributes.X_HMAC_AUTH_SIGNATURE).get(0), "user:0xGKsmKRrbz6txscdugd3PBTpNKlVfAohDS4js9k4sQ=");
        assertEquals(cr.getHeaders().get(HmacAttributes.X_HMAC_AUTH_DATE).get(0), "1970-01-02T10:17:36.789Z");
    }
}
