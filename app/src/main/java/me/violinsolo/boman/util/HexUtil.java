package me.violinsolo.boman.util;

/**
 * @author violinsolo
 * @version Boman v0.1
 * @createAt 2020/5/25 5:42 PM
 * @updateAt 2020/5/25 5:42 PM
 * <p>
 * Copyright (c) 2020 EmberXu.hack. All rights reserved.
 */
public class HexUtil {
    public static final String TAG = HexUtil.class.getSimpleName();

    /**
     * cast the byte array to plain hex string, eg.
     *      "8A 3F 9B"
     * @param data          the target byte array
     * @param addSpace      whether the :return string has space between each byte.
     * @param isBigEndian   parse the byte array in big-endian mode (0x123456) Or little-endian mode (0x563421).
     * @return              the plain hex string, eg. "8A 3F 9B" OR "8A3F9B"
     */
    public static String hexStr(final byte[] data, boolean addSpace, boolean isBigEndian) {
        StringBuilder sb = new StringBuilder();

        if (isBigEndian) {
            // if it is Big-Endian, the data array order is the order people can read naturally.
            for (int i = 0; i < data.length; i++) {
                sb.append(hex(data[i]));
                if (addSpace)
                    sb.append(" ");
            }
        }else {
            // if it is Little-Endian, the data array order need to access reversely before people can read.
            for (int i = data.length-1; i >= 0; i--) {
                sb.append(hex(data[i]));
                if (addSpace)
                    sb.append(" ");
            }
        }

        String content = sb.toString().trim();

        return content;
    }

    /**
     * cast the byte array into readable ascii-based string literals
     * @param data          the target byte array
     * @param isBigEndian   parse the byte array in big-endian mode (0x123456) Or little-endian mode (0x563421).
     * @return              string literals based on ascii parsed from byte array.
     */
    public static String str(final byte[] data, boolean isBigEndian) {
        StringBuilder sb = new StringBuilder();

        if (isBigEndian) {
            // if it is Big-Endian, the data array order is the order people can read naturally.
            for (int i = 0; i < data.length; i++) {
                sb.append(ascii(data[i]));
//                Log.i(TAG, "|"+ascii(data[i])+"|");
            }
        }else {
            // if it is Little-Endian, the data array order need to access reversely before people can read.
            for (int i = data.length-1; i >= 0; i--) {
                sb.append(ascii(data[i]));
            }
        }

        String content = sb.toString();

        return content;
    }

    /**
     * cast the byte array into a long number
     * @param data          the target byte array
     * @param isBigEndian   parse the byte array in big-endian mode (0x123456) Or little-endian mode (0x563421).
     * @return              3698l, 3000l
     */
    public static long lng(final byte[] data, boolean isBigEndian) {
        String content = hexStr(data, false, isBigEndian);
        long result = Long.parseLong(content, 16);
        return result;
    }


    /**
     * cast the byte array to plain hex string **IN BIG-ENDIAN MODE**, eg.
     *      "8A 3F 9B"
     * @param data      the target byte array
     * @param addSpace  whether the :return string has space between each byte.
     * @return          the plain hex string (BIG-ENDIAN MODE), eg. "8A 3F 9B" OR "8A3F9B"
     */
    public static String hexStrBigEndian(final byte[] data, boolean addSpace) {
        return hexStr(data, addSpace, true);
    }

    /**
     * cast the byte array to plain hex string **IN BIG-ENDIAN MODE, SPACE ENABLED**, eg.
     *      "8A 3F 9B"
     * @param data  the target byte array
     * @return      the plain hex string (BIG-ENDIAN MODE, WITH SPACE), eg. "8A 3F 9B"
     */
    public static String hexStrBigEndian(final byte[] data) {
        return hexStr(data, true, true);
    }

    /**
     * cast the byte array to plain hex string **IN LITTLE-ENDIAN MODE**, eg.
     *      "8A 3F 9B"
     * @param data      the target byte array
     * @param addSpace  whether the :return string has space between each byte.
     * @return          the plain hex string (LITTLE-ENDIAN MODE), eg. "8A 3F 9B"
     */
    public static String hexStrLittleEndian(final byte[] data, boolean addSpace) {
        return hexStr(data, addSpace, false);
    }

    /**
     * cast the byte array to plain hex string **IN LITTLE-ENDIAN MODE, SPACE ENABLED**, eg.
     *      "8A 3F 9B"
     * @param data  the target byte array
     * @return      the plain hex string (LITTLE-ENDIAN MODE, WITH SPACE), eg. "8A 3F 9B"
     */
    public static String hexStrLittleEndian(final byte[] data) {
        return hexStr(data, true, false);
    }


    /**
     * cast a byte into a hex string
     * @param b     target byte
     * @return      "0A", "F8"
     */
    private static String hex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    /**
     * cast a byte into ascii char
     * @param b     target byte
     * @return      '\n', ' ', 'A', 'a'
     */
    private static char ascii(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        int asciiVal = Integer.parseInt(hex, 16);

        return (char) asciiVal;
    }

    /**
     * cast a byte array to a short, 16 bits integer.
     * @param data
     * @return
     */
    public static short toShort(final byte[] data) {
        short l = 0;
        l |= data[0] & 0xFF;
        l <<= 8;
        l |= data[1] & 0xFF;
//        System.out.println("l:"+l);
        return l;
    }
}
