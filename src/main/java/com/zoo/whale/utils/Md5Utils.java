package com.zoo.whale.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5工具类
 *
 * @author AceCandy
 * @version 1.0
 * @date 2016年5月5日 下午7:43:01
 * @since 1.0
 */
public class Md5Utils {

    private static final Logger logger = LoggerFactory.getLogger(Md5Utils.class);

    /**
     * @param plainText 明文
     * @return 32位密文
     */
    // 解决中文问题

	/*public final static String MD5(String s) {
		char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		try {
			byte[] btInput = s.getBytes("utf8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}*/

	/*public static String cell32v2(String plainText, String charset) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes(charset));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return re_md5;
	}*/
    public static String cell32(String plainText) {
        String reMd5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes(StandardCharsets.UTF_8));
            //md.update(plainText.getBytes("gb2312"));
            byte[] b = md.digest();

            int i;

            StringBuilder buf = new StringBuilder();
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }

            reMd5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            logger.error("md5 exception! ", e);
        }
        return reMd5;
    }

	/*public static String md5Encode(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes("UTF-8"));
			return (Hex.encodeHexString(messageDigest.digest())).toLowerCase();
		} catch (Exception e) {
			return "";
		}
	}*/
	/*  public static String cell8(String plainText)
	  {
	   return cell32(plainText).substring(8,16);
	  }*/

    //public static void main(String[] args) {
		/*String line = "6212260200070503350";
		     int length = line.length();
		  String slength= Integer.toString(length);
		
		 String cardresult =Md5Utils.cell32(slength+line);
		    System.out.println(cardresult);
		   
		  String cardresult1 = cardresult.toUpperCase();*/

    //String cardresult2 = Md5Utils.cell32(
    //	"32321562POGBIGJH20313229JYMESYGCe10adc3949ba59abbe56e057f20f883e3715ff2f6856290f197d00ea6780618fc413ce459c3425127e29405db4571a42")
    // .toUpperCase();
    //String cardresult3 = Md5Utils.md5Encode("111eeee1");
    //System.out.println(cardresult2);
    //System.out.println(cardresult3);
    //}

}
