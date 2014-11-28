package de.otto.hmac.authentication;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.Instant;
import org.slf4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_DATE;
import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_SIGNATURE;
import static org.slf4j.LoggerFactory.getLogger;

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
        MessageDigest md5MessageDigest = getMD5Digest();
        md5MessageDigest.update(request.getBody());
        return createSignatureBase(request.getMethod(), request.getHeader(X_HMAC_AUTH_DATE), request.getRequestURI(), md5MessageDigest);
    }

    public static String createSignatureBase(final String method, final String dateHeader, final String requestUri,
            MessageDigest md5MessageDigest) {
        final StringBuilder builder = new StringBuilder();

        builder.append(method).append("\n");
        builder.append(dateHeader).append("\n");
        builder.append(requestUri).append("\n");
        builder.append(toMd5(md5MessageDigest));

        return builder.toString();
    }

    public static String createRequestSignature(final String method, final String dateHeader, final String requestUri,
                                                final String body, final String secretKey) {
        MessageDigest md5MessageDigest = getMD5Digest();
        md5MessageDigest.update(body.getBytes());
        return createRequestSignature(method, dateHeader, requestUri, md5MessageDigest, secretKey);
    }

    public static String createRequestSignature(final String method, final String dateHeader, final String requestUri,
            final MessageDigest md5MessageDigest, final String secretKey) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            final Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            final String signatureBase = createSignatureBase(method, dateHeader, requestUri, md5MessageDigest);
            final byte[] result = mac.doFinal(signatureBase.getBytes());
            return encodeBase64WithoutLinefeed(result);

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
            return encodeBase64WithoutLinefeed(result);
        }
        catch (final Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    protected static String encodeBase64WithoutLinefeed(byte[] result) {
        return Base64.encodeBase64String(result).trim();
    }

    public static MessageDigest getMD5Digest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    private static String toMd5(final byte[] body) {
        final MessageDigest md = getMD5Digest();
        md.update(body);
        return toMd5(md);
    }

    private static String toMd5(MessageDigest md) {
        return Hex.encodeHexString(md.digest());
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
