package com.d.lib.common.util;

import android.database.Cursor;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

    private IOUtils() {
    }

    public static byte[] is2ByteArray(InputStream is) {
        if (is == null) {
            return new byte[0];
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int read;
        byte[] data = new byte[4096];

        try {
            while ((read = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            }
        } catch (Exception ignored) {
            return new byte[0];
        } finally {
            IOUtils.closeQuietly(buffer);
        }

        return buffer.toByteArray();
    }

    public static ByteArrayOutputStream is2ByteArrayOutputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream;
        outputStream = new ByteArrayOutputStream();
        byte[] b = new byte[4096];
        int len;
        while ((len = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, len);
            outputStream.flush();
        }
        return outputStream;
    }

    public static boolean is2File(final InputStream is, final File file) {
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file));
            byte[] data = new byte[8192];
            int len;
            while ((len = is.read(data, 0, 8192)) != -1) {
                os.write(data, 0, len);
                os.flush();
            }
            return true;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(is);
            closeQuietly(os);
        }
    }

    /**
     * Closes {@code cursor}, ignoring any checked exceptions. Does nothing if {@code cursor} is
     * null.
     */
    public static void closeQuietly(Cursor cursor) {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Closes {@code closeable}, ignoring any checked exceptions. Does nothing if {@code closeable} is
     * null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }
}
