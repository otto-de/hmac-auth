package de.otto.hmac;

import org.apache.commons.codec.binary.Hex;

import javax.servlet.ServletInputStream;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Some ByteArray utilities.
 *
 * @author Guido Steinacker
 * @since 29.11.12
 */
public class ByteArrayUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    public static byte[] toByteArray(final ServletInputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[BUFFER_SIZE];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static String toMd5(byte[] body) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return Hex.encodeHexString(md.digest(body));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("should never happen", e);
        }
    }

}
