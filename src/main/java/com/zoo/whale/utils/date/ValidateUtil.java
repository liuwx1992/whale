package com.zoo.whale.utils.date;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Date: 2023年 12月 04日
 * @Time:11时:16分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class ValidateUtil {
    private static final String PATTERN_CELL = "^((\\+86)|(86)|(086))?1[3475689][0-9]\\d{8}$";
    private static final String PATTERN_data = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final String PATTERN_DATE = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final String PATTERN_MAIL = "^[0-9a-zA-Z_\\-\\.]+@(([0-9a-zA-Z]+)[.])+[0-9a-zA-Z\\-]{2,10}$";
    private static final String PATTERN_ID = "^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5])|71|(8[12])|91)\\d{4}((19\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(19\\d{2}(0[13578]|1[02])31)|(19\\d{2}02(0[1-9]|1\\d|2[0-9]))|(19([13579][26]|[2468][048]|0[48])0229)|(20\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(20\\d{2}(0[13578]|1[02])31)|(20\\d{2}02(0[1-9]|1\\d|2[0-9]))|(20([13579][26]|[2468][048]|0[48])0229))\\d{3}(\\d|X|x){1}$";
    private static final String PATTERN_ID_15 = "^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}";
    private static final String PATTERN_Gid = "^[0-9a-zA-Z]*$";
    private static final String PATTERN_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    private static final String PATTERN_CT = "^((133)|(153)|(177)|(18[0,1,9]))\\d{8}$";
    private static final String PATTERN_CM = "^((13[4-9])|(147)|(15[0-2,7-9])|(18[2-3,7-8]))\\d{8}$";
    private static final String PATTERN_CU = "^((13[0-2])|(145)|(15[5-6])|(186))\\d{8}$";
    private static final String PATTERN_NUM = "^[0-9]*$";
    private static final String PATTERN_DX = "^((133)|(153)|(173)|(18[0,1,9])|(177))\\d{8}$";
    private static final String PATTERN_LT = "^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}$";
    private static final String PATTERN_YD = "^((13[4-9])|(178)|(147)|(15[0-2,7-9])|(18[2-4,7-8]))\\d{8}$";
    private static final String PATTERN_DXXN = "^170[0-2]\\d{7}";
    private static final String PATTERN_LTXN = "^170[7-9]\\d{7}";
    private static final String PATTERN_YDXN = "^1705\\d{7}$";
    public static final String PATTERN_PHONE_CM = "^1(3[4-9]|4[7]|5[0-27-9]|7[08]|8[2-478]|9[8])\\d{8}$";
    public static final String PATTERN_PHONE_CU = "^1(3[0-2]|4[5]|5[56]|7[0156]|8[56]|6[6])\\d{8}$";

    public ValidateUtil() {
    }

    public static boolean checkCard(String card) {
        return card.length() >= 16 && card.length() <= 20 ? checkNumber(card) : false;
    }

    public static boolean checkNumber(String number) {
        return matcher("^[0-9]*$", number);
    }

    public static boolean checkBankId(String bankId) {
        if (bankId == null) {
            return false;
        } else {
            int size = bankId.length();

            try {
                Long.parseLong(bankId.substring(0, size / 2));
                Long.parseLong(bankId.substring(size / 2, size));
            } catch (Exception var3) {
                return false;
            }

            return size >= 13 && size <= 20;
        }
    }

    public static boolean checkMail(String mail) {
        return matcher("^[0-9a-zA-Z_\\-\\.]+@(([0-9a-zA-Z]+)[.])+[0-9a-zA-Z\\-]{2,10}$", mail);
    }

    public static boolean checkGid(String gid) {
        return matcher("^[0-9a-zA-Z]*$", gid);
    }

    public static boolean checkData(String data1) {
        return matcher("^\\d{4}-\\d{2}-\\d{2}$", data1);
    }

    public static boolean checkDateForSecond(String date) {
        return matcher("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$", date);
    }

    public static boolean checkId(String id) {
        if (id == null) {
            return false;
        } else {
            return id.length() == 15 ? matcher("^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}", id) : matcher("^((1[1-5])|(2[1-3])|(3[1-7])|(4[1-6])|(5[0-4])|(6[1-5])|71|(8[12])|91)\\d{4}((19\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(19\\d{2}(0[13578]|1[02])31)|(19\\d{2}02(0[1-9]|1\\d|2[0-9]))|(19([13579][26]|[2468][048]|0[48])0229)|(20\\d{2}(0[13-9]|1[012])(0[1-9]|[12]\\d|30))|(20\\d{2}(0[13578]|1[02])31)|(20\\d{2}02(0[1-9]|1\\d|2[0-9]))|(20([13579][26]|[2468][048]|0[48])0229))\\d{3}(\\d|X|x){1}$", id);
        }
    }

    public static boolean checkCell(String cell) {
        return matcher("^((\\+86)|(86)|(086))?1[3475689][0-9]\\d{8}$", cell);
    }

    public static boolean checkEmail(String email) {
        return matcher("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$", email);
    }

    public static boolean checkCT(String number) {
        return matcher("^((133)|(153)|(177)|(18[0,1,9]))\\d{8}$", number);
    }

    public static boolean checkCM(String number) {
        return matcher("^((13[4-9])|(147)|(15[0-2,7-9])|(18[2-3,7-8]))\\d{8}$", number);
    }

    public static boolean checkCU(String number) {
        return matcher("^((13[0-2])|(145)|(15[5-6])|(186))\\d{8}$", number);
    }

    public static boolean checkDX(String number) {
        return matcher("^((133)|(153)|(173)|(18[0,1,9])|(177))\\d{8}$", number);
    }

    public static boolean checkLT(String number) {
        return matcher("^((13[0-2])|(145)|(15[5-6])|(176)|(18[5,6]))\\d{8}$", number);
    }

    public static boolean checkYD(String number) {
        return matcher("^((13[4-9])|(178)|(147)|(15[0-2,7-9])|(18[2-4,7-8]))\\d{8}$", number);
    }

    public static boolean checkDXXN(String number) {
        return matcher("^170[0-2]\\d{7}", number);
    }

    public static boolean checkLTXN(String number) {
        return matcher("^170[7-9]\\d{7}", number);
    }

    public static boolean checkYDXN(String number) {
        return matcher("^1705\\d{7}$", number);
    }

    public static boolean checkNotEmpty(String string) {
        return StringUtils.isNotEmpty(string);
    }

    public static boolean matcher(String patternStr, String objStr) {
        if (StringUtils.isBlank(objStr)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(objStr);
            return matcher.find();
        }
    }

    public static boolean checkCheckCode(String jsonData, String apiCode, String tokenId, String checkCode) {
        return checkCode.equals(MD5Utils.cell32(jsonData + MD5Utils.cell32(apiCode + tokenId)));
    }

    public static void main(String[] args) {
    }
}
