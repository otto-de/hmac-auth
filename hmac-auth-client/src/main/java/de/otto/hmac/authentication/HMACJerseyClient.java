package de.otto.hmac.authentication;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import de.otto.hmac.HmacAttributes;
import de.otto.hmac.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;

public class HMACJerseyClient extends ApacheHttpClient {

    private String user;
    private String secretKey;
    private String method;
    private String date;
    private String requestUri;
    private ByteSource body;

    private HMACJerseyClient(final ClientConfig cc) {
        super(createDefaultClientHander(cc), null);
    }

    public HMACJerseyClient auth(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
        return this;
    }

    public WebResource.Builder authenticatedResource(final String url) throws IOException {
        assertAuthentificationPossible();
        date = new DateTime().toString();
        MessageDigest md5MessageDigest = evaluateMessageDigest(body);
        final StringBuilder builder = new StringBuilder(user);
        builder.append(":");
        builder.append(RequestSigningUtil.createRequestSignature(method, date, requestUri, md5MessageDigest, secretKey));
        return resource(url).header(HmacAttributes.X_HMAC_AUTH_SIGNATURE, builder.toString()).header(
                HmacAttributes.X_HMAC_AUTH_DATE, date);
    }

    private MessageDigest evaluateMessageDigest(ByteSource byteSource) throws IOException {
        MessageDigest md5MessageDigest = RequestSigningUtil.getMD5Digest();
        if(byteSource != null) {
            try (OutputStream out = new DigestOutputStream(ByteStreams.nullOutputStream(), md5MessageDigest)) {
                byteSource.copyTo(out);
            }
        }
        return md5MessageDigest;
    }

    private void assertAuthentificationPossible() throws IOException {
        Assert.isTrue(!StringUtils.isNullOrEmpty(user), "User is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(secretKey), "Secret key is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(method), "Method is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(requestUri), "URI is desired for authentication");
        if (method.equals("PUT") || method.equals("POST")) {
            Assert.isTrue(!isNullOrEmpty(body), "Body is required in PUT and POST");
        }
    }

    private boolean isNullOrEmpty(ByteSource byteSource) throws  IOException {
        return byteSource == null || byteSource.isEmpty();
    }

    private static ApacheHttpClientHandler createDefaultClientHander(final ClientConfig cc) {
        final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

        return new ApacheHttpClientHandler(client, cc);
    }

    public static HMACJerseyClient create() {
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_CHUNKED_ENCODING_SIZE, 50000);
        return new HMACJerseyClient(config);
    }

    public HMACJerseyClient withMethod(final String method) {
        this.method = method;
        return this;
    }

    public HMACJerseyClient withUri(final String uri) {
        this.requestUri = uri;
        return this;
    }

    public HMACJerseyClient withBody(final ByteSource body) {
        this.body = body;
        return this;
    }
}