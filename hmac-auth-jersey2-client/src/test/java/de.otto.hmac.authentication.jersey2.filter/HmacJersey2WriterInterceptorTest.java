package de.otto.hmac.authentication.jersey2.filter;

import com.google.common.io.ByteSource;
import de.otto.hmac.authentication.RequestSigningUtil;
import de.otto.hmac.authentication.WrappedRequest;
import de.otto.hmac.authentication.jersey2.test.JerseyServletTest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for Jersey2 client request filter and writer interceptor combination.
 */
public class HmacJersey2WriterInterceptorTest extends JerseyServletTest {

    private static final Clock TEST_CLOCK = Clock.fixed(LocalDateTime.of(2014, 2, 28, 9, 25).toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

    @Test
    public void shouldSetHmacHeadersOnGetRequest() {
        final String result = target("returnHmacSignature").request().get(String.class);

        final String requestSignature = result.split(";")[0];
        final String requestHeaderDateTime = result.split(";")[1];
        assertEquals("user:E4XWVFmMHFBO+NQvrQWhJY7hU8qUJsLQzyAJk0+UnBo=", requestSignature);
        assertEquals("2014-02-28T09:25:00Z", requestHeaderDateTime);

        // verify if signature has been correctly calculated
        final String hmacSignature = requestSignature.split(":")[1];
        final String computedSignature =
                createRequestSignature("GET", requestHeaderDateTime, "/returnHmacSignature", "".getBytes(), "secrectKey");

        assertEquals(hmacSignature, computedSignature);
    }

    @Test
    public void shouldSetHmacHeadersOnPostRequest() throws IOException {
        final Response response = target("returnHmacSignature").request().post(Entity.text("test"));

        assertEquals(200, response.getStatus());
        final InputStream responseContent = response.readEntity(InputStream.class);
        byte[] buffer = new byte[responseContent.available()];
        IOUtils.readFully(responseContent, buffer);
        final String result = new String(buffer, StandardCharsets.UTF_8);
        final String requestSignature = result.split(";")[0];
        final String requestHeaderDateTime = result.split(";")[1];
        assertEquals("user:+BvBUgz//6jg1EFvdf0iDqJTcTEc+dykuBYVo53kakU=", requestSignature);
        assertEquals("2014-02-28T09:25:00Z", requestHeaderDateTime);

        // verify if signature has been correctly calculated
        final String hmacSignature = requestSignature.split(":")[1];
        final String computedSignature =
                createRequestSignature("POST", requestHeaderDateTime, "/returnHmacSignature", "test".getBytes(), "secrectKey");

        assertEquals(hmacSignature, computedSignature);
    }

    @Path("returnHmacSignature")
    public static class HmacTestResource {

        @GET
        public String returnHmacSignatureOnGet(@HeaderParam("x-hmac-auth-signature") String hmacAuthSignature,
                                               @HeaderParam("x-hmac-auth-date") String hmacAuthDate,
                                               @Context HttpServletRequest request) throws IOException {
            assertTrue(RequestSigningUtil.checkRequest(WrappedRequest.wrap(request), "secrectKey", TEST_CLOCK));
            return hmacAuthSignature + ";" + hmacAuthDate;
        }

        @POST
        public String returnHmacSignatureOnPost(@HeaderParam("x-hmac-auth-signature") String hmacAuthSignature,
                                                @HeaderParam("x-hmac-auth-date") String hmacAuthDate,
                                                @Context HttpServletRequest request) throws IOException {
            assertTrue(RequestSigningUtil.checkRequest(WrappedRequest.wrap(request), "secrectKey", TEST_CLOCK));
            return hmacAuthSignature + ";" + hmacAuthDate;
        }
    }

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(HmacTestResource.class);
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(new HmacJersey2ClientRequestFilter("user", "secrectKey", TEST_CLOCK));
    }

    private String createRequestSignature(final String httpMethod, final String dateHeaderString, final String requestUri,
                                          final byte[] body, final String secretKey) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            final String signatureBase = RequestSigningUtil.createSignatureBase(httpMethod, dateHeaderString, requestUri, ByteSource.wrap(body));
            final byte[] result = mac.doFinal(signatureBase.getBytes());
            return encodeBase64WithoutLinefeed(result);
        } catch (final Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    protected static String encodeBase64WithoutLinefeed(byte[] result) {
        return Base64.encodeBase64String(result).trim();
    }

}
