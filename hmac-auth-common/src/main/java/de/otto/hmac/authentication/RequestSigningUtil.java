package de.otto.hmac.authentication;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;

import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_DATE;
import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_SIGNATURE;
import static org.slf4j.LoggerFactory.getLogger;

public class RequestSigningUtil {

    private static final Logger LOG = getLogger(RequestSigningUtil.class);

    public static boolean checkRequest(final WrappedRequest request, final String secretKey, final Clock clock) {

        if (!hasValidRequestTimeStamp(request, clock)) {
            return false;
        }

        final String requestSignature = getSignature(request);

        final String[] split = requestSignature.split(":");
        final String sentSignature = split[1];

        final String generatedSignature = createRequestSignature(request, secretKey);

        return generatedSignature.equals(sentSignature);
    }

    public static boolean hasValidRequestTimeStamp(final WrappedRequest request, final Clock clock) {
        final String requestTimeString = getDateFromHeader(request);
        if (requestTimeString == null || requestTimeString.isEmpty()) {
            LOG.error("Signierter Request enthält kein Datum.");
            return false;
        }

        final Instant serverTime = Instant.now(clock);
        final Instant requestTime = Instant.parse(requestTimeString);

        final TemporalAmount fiveMinutes = Duration.ofMinutes(5);

        final boolean inRange = requestTime.isAfter(serverTime.minus(fiveMinutes))
                && requestTime.isBefore(serverTime.plus(fiveMinutes));

        if (!inRange) {
            LOG.warn("Zeitstempel ausserhalb Serverzeit. Server: " + serverTime + ". Request: " + requestTimeString + ".");
        }

        return inRange;
    }

    public static String createSignatureBase(final WrappedRequest request) {
        return createSignatureBase(request.getMethod(), getDateFromHeader(request), request.getRequestURI(), request.getBody());
    }

    public static String createSignatureBase(final String method, final String dateHeader, final String requestUri, ByteSource body) {
        final StringBuilder builder = new StringBuilder();

        builder.append(method).append("\n");
        builder.append(dateHeader).append("\n");
        builder.append(requestUri).append("\n");
        builder.append(toMd5Hex(body));

        return builder.toString();
    }

    public static String createRequestSignature(final String method, final String dateHeader, final String requestUri, ByteSource body, final String secretKey) {
        final String signatureBase = createSignatureBase(method, dateHeader, requestUri, body);
        return createRequestSignature(signatureBase, secretKey);
    }

    public static String createRequestSignature(String signatureBase, String secretKey) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            final byte[] result = mac.doFinal(signatureBase.getBytes());
            return encodeBase64WithoutLinefeed(result);
        }
        catch (final Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    public static String createRequestSignature(final WrappedRequest request, final String secretKey) {
        final String signatureBase = createSignatureBase(request);
        return createRequestSignature(signatureBase, secretKey);
    }

    protected static String encodeBase64WithoutLinefeed(byte[] result) {
        return Base64.encodeBase64String(result).trim();
    }

    public static boolean hasSignature(final HttpServletRequest request) {
        return request.getHeader(X_HMAC_AUTH_SIGNATURE) != null;
    }

    public static String getSignature(final HttpServletRequest request) {
        return request.getHeader(X_HMAC_AUTH_SIGNATURE);
    }

    public static String getDateFromHeader(final HttpServletRequest request) {
        final String header = request.getHeader(X_HMAC_AUTH_DATE);
        if (header == null) {
            return "";
        }
        return header;
    }

    public static String toMd5Hex(ByteSource byteSource) {
        try {
            HashCode md5 = byteSource.hash(Hashing.md5());
            return md5.toString();
        } catch (IOException e) {
            throw new RuntimeException("error evaluating md5 sum", e);
        }
    }
}
