package de.otto.hmac;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Some String utilities.
 *
 * @author Guido Steinacker
 * @since 29.11.12
 */
public class StringUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    public static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }

    public static String toString(final InputStream is) throws IOException {
        StringWriter sw = new StringWriter();
        InputStreamReader in = new InputStreamReader(is);
        char[] buffer = new char[BUFFER_SIZE];
        int n = 0;
        while (-1 != (n = in.read(buffer))) {
            sw.write(buffer, 0, n);
        }
        return sw.toString();
    }

}
