package com.zoo.whale.utils.shai;

/**
 * @ClassName:ByteUtil
 * @Description:
 * @Author:weixin.liu
 * @Date:2021/11/29
 * @Time:20:53
 * @Version；1.0
 */
public class ByteUtil {

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static String byteToString(byte[] ba) {
        String s;
        s = "";
        for (int i = 0; i < ba.length; i++) {
            s += String.format("%02X", ba[i]);
        }
        return s;
    }

    public static byte[] hexStringToByteArray(String data) {
        if (data == null || data.length() == 0 || (data.length() % 2) != 0) {
            return null;
        }
        int len = data.length() / 2;
        byte[] result = new byte[len];
        String tmp;
        for (int i = 0; i < len; i++) {
            tmp = data.substring(i * 2, (i + 1) * 2);
            try {
                result[i] = (byte) Integer.parseInt(tmp, 16);
            } catch (Exception e) {

                result[i] = 0x00;
            }
        }
        return result;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
