package com.zoo.whale.utils.ice;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtil {
	public static String getNowyyyy_MM_dd_HH_mm_ss(){
		Date date = new Date(System.currentTimeMillis());
		return formatDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	public static String getNowyyyyMMddHHmmss(){
		Date date = new Date(System.currentTimeMillis());
		return formatDate(date,"yyyyMMddHHmmss");
	}
	public static String getNowyyyyMMdd(){
		Date date = new Date(System.currentTimeMillis());
		return formatDate(date,"yyyyMMdd");
	}
	
	public static String getNowyyyy_MM_dd(){
		Date date = new Date(System.currentTimeMillis());
		return formatDate(date,"yyyy-MM-dd");
	}
	public static String getNowyyyy_MM(){
      Date date = new Date(System.currentTimeMillis());
      return formatDate(date,"yyyy-MM");
  }
	public static Date GetDate (String date,String Format) throws ParseException{
		  Date tmpdate = null;
		DateFormat df = new SimpleDateFormat(Format);
		 tmpdate=df.parse(date);
		return tmpdate; 
		
	}
	public static String getYesyyyy_MM_dd(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		return formatDate(calendar.getTime(),"yyyy-MM-dd");
	}
    /**
     * 格式化日期
     * @param dateStr 字符型日期
     * @param format 格式
     * @return 返回日期
     */
    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat df = new SimpleDateFormat(format);
            String dt = dateStr.replaceAll("-", "/");
            if ((!dt.equals("")) && (dt.length() < format.length())) {
                dt += format.substring(dt.length()).replaceAll("[YyMmDdHhSs]",
                        "0");
            }
            date = (Date) df.parse(dt);
        } catch (Exception e) {
        }
        return date;
    }

    /**
     * @param dateStr
     * @return Date
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy/MM/dd");
    }

    /**
     * 格式化输出日期
     * @param date 日期
     * @param format  格式
     * @return 返回字符型日期
     */
    public static String format(Date date, String format) {
        String result = "";
        try {
            if (date != null) {
                DateFormat df = new SimpleDateFormat(format);
                result = df.format(date);
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 常用的格式化日期
     * @param date Date
     * @return String
     */
    public static String formatDate(Date date, String format) {
        return format(date, format);
    }
    /**
     * 常用的格式化日期
     * @param date Date
     * @return String
     */
    public static String formatDate(Date date) {
        return format(date,"yyyy-MM-dd");
    }
    /**
     * 输出时间
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, "yyyy-MM-dd");
    }
    
    /**
     * 返回年份
     * @param date 日期
     * @return 返回年份
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 返回月份
     * @param date 日期
     * @return 返回月份
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    /**
     * 返回日份
     * @param date 日期
     * @return 返回日份
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 返回小时
     * @param date 日期
     * @return 返回小时
     */
    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 返回分钟
     * @param date 日期
     * @return 返回分钟
     */
    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 返回秒钟
     * @param date 日期
     * @return 返回秒钟
     */
    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.SECOND);
    }

    /**
     * 返回毫秒
     * @param date 日期
     * @return 返回毫秒
     */
    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 返回字符型日期
     * @param date 日期
     * @return 返回字符型日期
     */
    public static String getDate(Date date) {
        return format(date, "yyyy/MM/dd");
    }

    /**
     * 返回字符型时间
     * @param date 日期
     * @return 返回字符型时间
     */
    public static String getTime(Date date) {
        return format(date, "HH:mm:ss");
    }

    /**
     * 返回字符型日期时间
     * @param date 日期
     * @return 返回字符型日期时间
     */
    public static String getDateTime(Date date) {
        return format(date, "yyyy/MM/dd HH:mm:ss");
    }

    /**
     * 日期相加
     * @param date 日期
     * @param day 天数
     * @return 返回相加后的日期
     */
    public static Date addDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
        return c.getTime();
    }

    /**
     * 日期相减
     * @param date 日期
     * @param date1 日期
     * @return 返回相减后的日期
     */
    public static int diffDate(Date date, Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 取得指定月份的第一天
     * @param strdate String
     * @return String
     */
    public static String getMonthBegin(String strdate) {
        Date date = parseDate(strdate);
        return format(date, "yyyy-MM") + "-01";
    }

    /**
     * 取得指定月份的最后一天
     * @param strdate String
     * @return String
     */
    public static String getMonthEnd(String strdate) {
        Date date = parseDate(getMonthBegin(strdate));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return formatDate(calendar.getTime());
    }

    /**
     * 计算传入值是否星期(六)
     * @param str
     * @return boolean - 是返回true
     */
    public boolean checkWeek6(String str, int week) {
        boolean flag = false;
        int realWeek = 0;
        str=str.replace('/', '-');
        Calendar cal = Calendar.getInstance();
        cal.setTime(java.sql.Date.valueOf(str.substring(0, 10)));
        realWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (realWeek == (week + 1)) {
            flag = true;
        }
        return flag;
    }

    /**
     * Description: 获取GMT8时间
     * 
     * @return 将当前时间转换为GMT8时区后的Date
     */
    public static Date getGMT8Time() {
        Date gmt8 = null;
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"),
                    Locale.CHINESE);
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            day.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            day.set(Calendar.DATE, cal.get(Calendar.DATE));
            day.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
            day.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
            day.set(Calendar.SECOND, cal.get(Calendar.SECOND));
            gmt8 = day.getTime();
        } catch (Exception e) {
            System.out.println("获取GMT8时间 getGMT8Time() error !");
            e.printStackTrace();
            gmt8 = null;
        }
        return gmt8;
    }
    
    
    
    /**
	 * 将字符串转化为日期类型
	 * 
	 * @param pattern
	 *            转化格式
	 * @param dateStr
	 *            要转化的字符串
	 * @return
	 */
	public static Date getDate(String pattern, String dateStr) {
		Date date = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat(pattern);
			date = formatter.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	
//    public static void main(String args[])
//    {
//    	String s=getNowyyyy_MM_dd_HH_mm_ss();
//    	System.out.print(s);
//  
//    	
//   	
//
//    
//    	
//    }
}

