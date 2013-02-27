package de.otto.hmac.authentication;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.joda.time.DateTime;
import org.springframework.util.Assert;

import de.otto.hmac.HmacAttributes;
import de.otto.hmac.StringUtils;

public class HMACJerseyClient extends ApacheHttpClient4 {

    private String user;
    private String secretKey;
    private String method;
    private String date;
    private String requestUri;
    private String body = "";

    private HMACJerseyClient(final ClientConfig cc) {
        super(createDefaultClientHandler(), cc, null);
    }

    public HMACJerseyClient auth(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
        return this;
    }

    public WebResource.Builder authenticatedResource(final String url) {
        assertAuthentificationPossible();
        date = new DateTime().toString();
        final StringBuilder builder = new StringBuilder(user);
        builder.append(":");
        builder.append(RequestSigningUtil.createRequestSignature(method, date, requestUri, body, secretKey));
        return resource(url).header(HmacAttributes.X_HMAC_AUTH_SIGNATURE, builder.toString()).header(
                HmacAttributes.X_HMAC_AUTH_DATE, date);
    }

    private void assertAuthentificationPossible() {
        Assert.isTrue(!StringUtils.isNullOrEmpty(user), "User is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(secretKey), "Secret key is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(method), "Method is desired for authentication");
        Assert.isTrue(!StringUtils.isNullOrEmpty(requestUri), "URI is desired for authentication");
        if (method.equals("PUT") || method.equals("POST")) {
            Assert.isTrue(!StringUtils.isNullOrEmpty(body), "Body is required in PUT and POST");
        }
    }

    private static ApacheHttpClient4Handler createDefaultClientHandler() {
        final HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager());

        return new ApacheHttpClient4Handler(client, createCookieStore(), false);
    }

    private static BasicCookieStore createCookieStore() {
        return new BasicCookieStore();
    }

    public static HMACJerseyClient create() {
        return create(new DefaultApacheHttpClient4Config());
    }

    public static HMACJerseyClient create(final ClientConfig cc) {
        return new HMACJerseyClient(cc);
    }

    public HMACJerseyClient withMethod(final String method) {
        this.method = method;
        return this;
    }

    public HMACJerseyClient withUri(final String uri) {
        this.requestUri = uri;
        return this;
    }

    public HMACJerseyClient withBody(final String body) {
        this.body = body;
        return this;
    }
}