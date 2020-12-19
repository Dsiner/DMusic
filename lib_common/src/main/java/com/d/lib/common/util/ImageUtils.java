package com.d.lib.common.util;

import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

public class ImageUtils {
    private static final byte[] JPEG_SIGNATURE = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    private ImageUtils() {
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file input stream
     */
    public static boolean isJPG(InputStream is) {
        if (is == null) {
            return false;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int read;
        byte[] data = new byte[3];

        try {
            if ((read = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, read);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(buffer);
        }
        return isJPG(data);
    }

    @Deprecated
    public static boolean isJPG(byte[] data) {
        if (data == null || data.length < 3) {
            return false;
        }
        byte[] signatureB = new byte[]{data[0], data[1], data[2]};
        return Arrays.equals(JPEG_SIGNATURE, signatureB);
    }

    public static int getImageDegree(String filename) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(filename);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
     */
    @Deprecated
    public static int getOrientation(InputStream is) {
        return getOrientation(IOUtils.is2ByteArray(is));
    }

    @Deprecated
    private static int getOrientation(byte[] jpeg) {
        if (jpeg == null) {
            return 0;
        }

        int offset = 0;
        int length = 0;

        // ISO/IEC 10918-1:1993(E)
        while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
            int marker = jpeg[offset] & 0xFF;

            // Check if the marker is a padding.
            if (marker == 0xFF) {
                continue;
            }
            offset++;

            // Check if the marker is SOI or TEM.
            if (marker == 0xD8 || marker == 0x01) {
                continue;
            }
            // Check if the marker is EOI or SOS.
            if (marker == 0xD9 || marker == 0xDA) {
                break;
            }

            // Get the length and check if it is reasonable.
            length = pack(jpeg, offset, 2, false);
            if (length < 2 || offset + length > jpeg.length) {
                Log.e("Get orientation", "Invalid length");
                return 0;
            }

            // Break if the marker is EXIF in APP1.
            if (marker == 0xE1 && length >= 8
                    && pack(jpeg, offset + 2, 4, false) == 0x45786966
                    && pack(jpeg, offset + 6, 2, false) == 0) {
                offset += 8;
                length -= 8;
                break;
            }

            // Skip other markers.
            offset += length;
            length = 0;
        }

        // JEITA CP-3451 Exif Version 2.2
        if (length > 8) {
            // Identify the byte order.
            int tag = pack(jpeg, offset, 4, false);
            if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                Log.e("Get orientation", "Invalid byte order");
                return 0;
            }
            boolean littleEndian = (tag == 0x49492A00);

            // Get the offset and check if it is reasonable.
            int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
            if (count < 10 || count > length) {
                Log.e("Get orientation", "Invalid offset");
                return 0;
            }
            offset += count;
            length -= count;

            // Get the count and go through all the elements.
            count = pack(jpeg, offset - 2, 2, littleEndian);
            while (count-- > 0 && length >= 12) {
                // Get the tag and check if it is orientation.
                tag = pack(jpeg, offset, 2, littleEndian);
                if (tag == 0x0112) {
                    int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                    switch (orientation) {
                        case 1:
                            return 0;
                        case 3:
                            return 180;
                        case 6:
                            return 90;
                        case 8:
                            return 270;
                    }
                    Log.e("Get orientation", "Unsupported orientation");
                    return 0;
                }
                offset += 12;
                length -= 12;
            }
        }

        Log.e("Get orientation", "Orientation not found");
        return 0;
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }

        int value = 0;
        while (length-- > 0) {
            value = (value << 8) | (bytes[offset] & 0xFF);
            offset += step;
        }
        return value;
    }
}
