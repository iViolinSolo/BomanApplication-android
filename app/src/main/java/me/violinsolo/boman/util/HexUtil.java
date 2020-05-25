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


    public static String hexStrBigEndian(final byte[] data, boolean addSpace) {
        return hexStr(data, addSpace, true);
    }

    public static String hexStrBigEndian(final byte[] data) {
        return hexStr(data, true, true);
    }

    public static String hexStrLittleEndian(final byte[] data, boolean addSpace) {
        return hexStr(data, addSpace, false);
    }

    public static String hexStrLittleEndian(final byte[] data) {
        return hexStr(data, true, false);
    }


    public static String str(final byte[] data, boolean isBigEndian) {
        StringBuilder sb = new StringBuilder();

        if (isBigEndian) {
            // if it is Big-Endian, the data array order is the order people can read naturally.
            for (int i = 0; i < data.length; i++) {
                sb.append(ascii(data[i]));
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

    private static String hex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        return hex;
    }

    private static char ascii(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        int asciiVal = Integer.parseInt(hex, 16);

        return (char) asciiVal;
    }
}
