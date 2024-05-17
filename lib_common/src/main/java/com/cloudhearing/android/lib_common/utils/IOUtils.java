package com.cloudhearing.android.lib_common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class IOUtils {

    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * Reads and returns the rest of the given input stream as a byte array,
     * closing the input stream afterwards.
     * @param is the input stream.
     * @return the rest of the given input stream.
     */
    public static byte[] toByteArray(InputStream is) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            final byte[] b = new byte[BUFFER_SIZE];
            int n = 0;
            while ((n = is.read(b)) != -1) {
                output.write(b, 0, n);
            }
            return output.toByteArray();
        } finally {
            output.close();
        }
    }
}
