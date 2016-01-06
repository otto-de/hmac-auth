package de.otto.hmac.authentication.jersey2.filter;

import org.joda.time.Instant;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.net.URI;

/**
 * Jersey2 WriterInterceptor for HMAC request signatures. Register a HMAC client request filter and writer interceptor combination
 * with the HmacJerseyHelper for example:
 * <p/>
 * <code>
 * Client client = ClientBuilder.newBuilder().build();
 * HmacJerseyHelper.registerHmacFilter(client, "user", "hmacSecret");
 * </code>
 *
 * The HmacJersey2WriterInterceptor create a signature for http requests with payload, like POST and PUT.
 *
 * @author <a href="mailto:mathias.arens@googlemail.com">Mathias Arens</a>
 */
public class HmacJersey2WriterInterceptor implements WriterInterceptor {

    private String user;
    private String secretKey;

    public HmacJersey2WriterInterceptor(final String user, final String secretKey) {
        this.user = user;
        this.secretKey = secretKey;
    }

    @Override
    public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
        final Instant now = (Instant) context.getProperty(HmacJersey2ClientRequestFilter.NOW);
        final String httpMethod = (String) context.getProperty(HmacJersey2ClientRequestFilter.HTTP_METHOD);
        final URI uri = (URI) context.getProperty(HmacJersey2ClientRequestFilter.HTTP_URI);
        context.setOutputStream(
                new HmacJerseyOutputStreamWrapper(httpMethod, uri, now, user, secretKey, context, context.getOutputStream()));
        context.proceed();
    }

}
