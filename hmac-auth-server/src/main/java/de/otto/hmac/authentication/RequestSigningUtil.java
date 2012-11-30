package de.otto.hmac.authentication;

import de.otto.hmac.HmacAttributes;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.Instant;
import org.slf4j.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_DATE;
import static de.otto.hmac.HmacAttributes.X_HMAC_AUTH_SIGNATURE;
import static org.slf4j.LoggerFactory.getLogger;

public class RequestSigningUtil {

    private static final Logger LOG = getLogger(RequestSigningUtil.class);

    public static boolean checkRequest(WrappedRequest request, String secretKey) {

        if (!hasValidRequestTimeStamp(request)) {
            return false;
        }

        String requestSignature = request.getHeader(X_HMAC_AUTH_SIGNATURE);

        String[] split = requestSignature.split(":");
        String sentSignature = split[1];

        String generatedSignature = createRequestSignature(request, secretKey);

        return generatedSignature.equals(sentSignature);
    }


    public static boolean hasValidRequestTimeStamp(WrappedRequest request) {
        String requestTimeString = getDateFromHeader(request);
        if (requestTimeString == null || requestTimeString.isEmpty()) {
            LOG.error("Signierter Request enthält kein Datum.");
            return false;
        }

        Instant serverTime = new Instant();
        Instant requestTime = new Instant(requestTimeString);

        long fiveMinutes = 60 * 5000L;

        boolean inRange = requestTime.isAfter(serverTime.minus(fiveMinutes)) && requestTime.isBefore(serverTime.plus(fiveMinutes));

        if (!inRange) {
            LOG.warn("Zeitstempel ausserhalb Serverzeit. Server: " + serverTime + ". Request: " + requestTimeString + ".");
        }

        return inRange;
    }


    public static String createSignatureBase(WrappedRequest request) {
        StringBuilder builder = new StringBuilder();

        builder.append(request.getMethod()).append("\n");
        builder.append(request.getHeader(X_HMAC_AUTH_DATE)).append("\n");
        builder.append(request.getRequestURI()).append("\n");
        builder.append(toMd5(request.getBody()));

        return builder.toString();
    }

    public static String createRequestSignature(WrappedRequest request, String secretKey) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(keySpec);
            String signatureBase = createSignatureBase(request);
            byte[] result = mac.doFinal(signatureBase.getBytes());
            return Base64.encodeBase64String(result);

        } catch (Exception e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    private static String toMd5(String body) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return Hex.encodeHexString(md.digest(body.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new RuntimeException("should never happen", e);
        }
    }

    public static boolean hasSignature(HttpServletRequest request) {
        return request.getHeader(X_HMAC_AUTH_SIGNATURE) != null;
    }

    public static String getSignature(HttpServletRequest request) {
        return request.getHeader(X_HMAC_AUTH_SIGNATURE);
    }

    public static String getDateFromHeader(HttpServletRequest request) {
        String header = request.getHeader(X_HMAC_AUTH_DATE);
        if (header == null) {
            return "";
        }

        return header;
    }
}
