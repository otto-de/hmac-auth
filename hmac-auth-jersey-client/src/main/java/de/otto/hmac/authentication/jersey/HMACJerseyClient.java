package de.otto.hmac.authentication.jersey;

import com.google.common.io.ByteSource;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import com.sun.jersey.client.apache4.config.DefaultApacheHttpClient4Config;
import de.otto.hmac.HmacAttributes;
import de.otto.hmac.StringUtils;
import de.otto.hmac.authentication.RequestSigningUtil;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.time.Clock;
import java.time.ZonedDateTime;

public class HMACJerseyClient extends ApacheHttpClient4 {

    private String user;
    private String secretKey;
    private String method;
    private String date;
    private String requestUri;
    private ByteSource body = ByteSource.empty();
    private final Clock clock;

    private HMACJerseyClient(final ClientConfig cc, final Clock clock) {
        super(createDefaultClientHander(cc));
        this.clock = clock;
    }

    public HMACJerseyClient auth(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
        return this;
    }

    public WebResource.Builder authenticatedResource(final String url) throws IOException {
        assertAuthentificationPossible();
        date = ZonedDateTime.now(clock).toString();
        final StringBuilder builder = new StringBuilder(user);
        builder.append(":");
        builder.append(RequestSigningUtil.createRequestSignature(method, date, requestUri, body, secretKey));
        return resource(url).header(HmacAttributes.X_HMAC_AUTH_SIGNATURE, builder.toString()).header(
                HmacAttributes.X_HMAC_AUTH_DATE, date);
    }

    private void assertAuthentificationPossible() throws IOException {
        validateNullOrEmpty(user);
        validateNullOrEmpty(secretKey);
        validateNullOrEmpty(method);
        validateNullOrEmpty(requestUri);
    }

    private void validateNullOrEmpty(String property) {
        if (StringUtils.isNullOrEmpty(property)) {
            throw new IllegalArgumentException("argument " + property + " is empty or null");
        }
    }

    private static ApacheHttpClient4Handler createDefaultClientHander(final ClientConfig cc) {
        final HttpParams params = new BasicHttpParams();
        final int maxConnections = 20;
        final ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager();
        manager.setDefaultMaxPerRoute(maxConnections);
        manager.setMaxTotal(maxConnections);
        return new ApacheHttpClient4Handler(new DefaultHttpClient(manager, params), null, false);
    }

    public static HMACJerseyClient create() {
        return create(Clock.systemUTC());
    }

    public static HMACJerseyClient create(final Clock clock) {
        DefaultApacheHttpClient4Config config = new DefaultApacheHttpClient4Config();
        return new HMACJerseyClient(config, clock);
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