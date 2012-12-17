package de.otto.hmac.authentication;

import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_DATE;
import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_SIGNATURE;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.Instant;
import org.slf4j.Logger;

public class RequestSigningUtil {

    private static final Logger LOG = getLogger(RequestSigningUtil.class);

    public static boolean checkRequest(final WrappedRequest request, final String secretKey) {

        if (!hasValidRequestTimeStamp(request)) {
            return false;
        }

        final String requestSignature = request.getHeader(X_HMAC_AUTH_SIGNATURE);

        final String[] split = requestSignature.split(":");
        final String sentSignature = split[1];

        final String generatedSignature = createRequestSignature(request, secretKey);

        return generatedSignature.equals(sentSignature);
    }

    public static boolean hasValidRequestTimeStamp(final WrappedRequest request) {
        final String requestTimeString = getDateFromHeader(request);
        if (requestTimeString == null || requestTimeString.isEmpty()) {
            LOG.error("Signierter Request enthält kein Datum.");
            return false;
        }

        final Instant serverTime = new Instant();
        final Instant requestTime = new Instant(requestTimeString);

        final long fiveMinutes = 60 * 5000L;

        final boolean inRange = requestTime.isAfter(serverTime.minus(fiveMinutes))
                && requestTime.isBefore(serverTime.plus(fiveMinutes));

        if (!inRange) {
            LOG.warn("Zeitstempel ausserhalb Serverzeit. Server: " + serverTime + ". Request: " + requestTimeString + ".");
        }

        return inRange;
    }

    public static String createSignatureBase(final WrappedRequest request) {
        return createSignatureBase(request.getMethod(), request.getHeader(X_HMAC_AUTH_DATE), request.getRequestURI(),
                request.getBody());
    }

    public static String createSignatureBase(final String method, final String dateHeader, final String requestUri,
            final String body) {
        final StringBuilder builder = new StringBuilder();

        builder.append(method).append("\n");
        builder.append(dateHeader).append("\n");
        builder.append(requestUri).append("\n");
        builder.append(toMd5(body));

        return builder.toString();
    }

    public static String createRequestSignature(final String method, final String dateHeader, final String requestUri,
            final String body, final String secretKey) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            final String signatureBase = createSignatureBase(method, dateHeader, requestUri, body);
            final byte[] result = mac.doFinal(signatureBase.getBytes());
            return Base64.encodeBase64String(result);

        }
        catch (final Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    public static String createRequestSignature(final WrappedRequest request, final String secretKey) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            final String signatureBase = createSignatureBase(request);
            final byte[] result = mac.doFinal(signatureBase.getBytes());
            return Base64.encodeBase64String(result);
        }
        catch (final Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    private static String toMd5(final String body) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            return Hex.encodeHexString(md.digest(body.getBytes("UTF-8")));
        }
        catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("should never happen", e);
        }
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
}
