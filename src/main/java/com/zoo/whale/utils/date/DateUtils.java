package com.zoo.whale.utils.date;

import org.apache.commons.lang.math.NumberUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Date: 2023年 12月 04日
 * @Time:11时:15分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class DateUtils {
    public static final String yyyy_MM = "yyyy-MM";
    public static final String yyyyMM = "yyyyMM";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMdd = "yyyyMMdd";
    private static final long ONE_DAY_MILL_SECOND = 86400000L;
    public static final String yyyy_MM_dd_pattern = "^((\\d{2}(([02468][048])|([13579][26]))-((((0[13578])|(1[02]))-((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))-((0[1-9])|([1-2][0-9])|(30)))|(02-((0[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))-((((0[13578])|(1[02]))-((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))-((0[1-9])|([1-2][0-9])|(30)))|(02-((0[1-9])|(1[0-9])|(2[0-8]))))))$";
    public static final String yyyyMMdd_pattern = "^((\\d{2}(([02468][048])|([13579][26]))((((0[13578])|(1[02]))((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))((0[1-9])|([1-2][0-9])|(30)))|(02((0[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))((((0[13578])|(1[02]))((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))((0[1-9])|([1-2][0-9])|(30)))|(02((0[1-9])|(1[0-9])|(2[0-8]))))))$";
    private static final Object lockObj = new Object();
    private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap();

    public DateUtils() {
    }

    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = (ThreadLocal)sdfMap.get(pattern);
        if (tl == null) {
            synchronized(lockObj) {
                tl = (ThreadLocal)sdfMap.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        protected SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(pattern);
                        }
                    };
                    sdfMap.put(pattern, tl);
                }
            }
        }

        return (SimpleDateFormat)tl.get();
    }

    public static String format(Date date, String pattern) {
        return getSdf(pattern).format(date);
    }

    public static String format(Date date) {
        return getSdf("yyyy-MM-dd").format(date);
    }

    public static Date parse(String dateStr, String pattern) throws ParseException {
        return getSdf(pattern).parse(dateStr);
    }

    public static long getDate(long time) {
        Date date = new Date(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTimeInMillis();
    }

    public static String getNowyyyy_MM_dd() {
        Date date = new Date(System.currentTimeMillis());
        return format(date, "yyyy-MM-dd");
    }

    public static Date getDate2(String str) {
        SimpleDateFormat FORMAT_DATE_2_PATTERN = new SimpleDateFormat("yyyyMMdd");

        try {
            return FORMAT_DATE_2_PATTERN.parse(str);
        } catch (ParseException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static String formatForDate2(Date date) {
        SimpleDateFormat FORMAT_DATE_2_PATTERN = new SimpleDateFormat("yyyyMMdd");
        return FORMAT_DATE_2_PATTERN.format(date);
    }

    public static int addDay(int currentDay, int addDay) {
        Date currentDate = getDate2(currentDay + "");
        String addDateStr = formatForDate2(new Date(currentDate.getTime() + 86400000L * (long)addDay));
        return NumberUtils.toInt(addDateStr);
    }

    public static int dayDiffer(String startTime, String endTime) throws Exception {
        Date startDate = getDate2(startTime);
        Date endDate = getDate2(endTime);
        if (startDate != null && endDate != null) {
            long timeDiff = timeDiffer(startDate, endDate);
            long modValue = timeDiff % 86400000L;
            return (int)(timeDiff / 86400000L + (long)(modValue == 0L ? 0 : 1));
        } else {
            throw new Exception("日期格式错误.");
        }
    }

    public static long timeDiffer(Date startTime, Date endTime) {
        long time = endTime.getTime() - startTime.getTime();
        return time >= 0L ? time : -time;
    }

    public static void main(String[] args) {
        System.out.println(format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

    public static boolean checkDateYyyy_MM_dd(String dateStr) {
        return ValidateUtil.matcher("^((\\d{2}(([02468][048])|([13579][26]))-((((0[13578])|(1[02]))-((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))-((0[1-9])|([1-2][0-9])|(30)))|(02-((0[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))-((((0[13578])|(1[02]))-((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))-((0[1-9])|([1-2][0-9])|(30)))|(02-((0[1-9])|(1[0-9])|(2[0-8]))))))$", dateStr);
    }

    public static boolean checkDateYyyyMMdd(String dateStr) {
        return ValidateUtil.matcher("^((\\d{2}(([02468][048])|([13579][26]))((((0[13578])|(1[02]))((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))((0[1-9])|([1-2][0-9])|(30)))|(02((0[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))((((0[13578])|(1[02]))((0[1-9])|([1-2][0-9])|(3[01])))|(((0[469])|(11))((0[1-9])|([1-2][0-9])|(30)))|(02((0[1-9])|(1[0-9])|(2[0-8]))))))$", dateStr);
    }
}
