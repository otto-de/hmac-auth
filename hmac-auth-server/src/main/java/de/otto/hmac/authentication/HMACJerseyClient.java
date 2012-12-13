package de.otto.hmac.authentication;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;

import de.otto.hmac.HmacAttributes;

public class HMACJerseyClient extends ApacheHttpClient {

    private String user;
    private String secretKey;
    private String method;
    private String date;
    private String requestUri;
    private String body;

    private HMACJerseyClient(final ClientConfig cc) {
        super(createDefaultClientHander(cc), null);
    }

    public HMACJerseyClient auth(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
        return this;
    }

    public WebResource.Builder authenticatedResource(final String url) {
        final StringBuilder builder = new StringBuilder(user);
        builder.append(":");
        builder.append(RequestSigningUtil.createRequestSignature(method, date, requestUri, body, secretKey));
        return resource(url).header(HmacAttributes.X_HMAC_AUTH_SIGNATURE, builder.toString());
    }

    private static ApacheHttpClientHandler createDefaultClientHander(final ClientConfig cc) {
        final HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

        return new ApacheHttpClientHandler(client, cc);
    }

    public static HMACJerseyClient create() {
        return create(new DefaultApacheHttpClientConfig());
    }

    public static HMACJerseyClient create(final ClientConfig cc) {
        return new HMACJerseyClient(cc);
    }

    public HMACJerseyClient withMethod(final String method) {
        this.method = method;
        return this;
    }

    public HMACJerseyClient withDate(final String date) {
        this.date = date;
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