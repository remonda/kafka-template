package cn.focusmedia.consumer.tools;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * TODO <br/>
 * ClassName：DateUtil <br/>
 * Author：QGX <br/>
 * Date：2019/11/4 22:32 <br/>
 * Version：1.0
 */
public class DateUtil {
    private static DateTimeFormatter utc = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(DateTimeZone.UTC);
    private final static Logger LOGGER = Logger.getLogger(DateUtil.class);
    /**
     * @param UTC 传入的UTC时间
     * @return 上海时间毫秒值
     */
    public static long getTimeInMillis(String UTC) {
        DateTime utcTime = utc.parseDateTime(UTC);
//        long timeInMillis = utcTime.toCalendar(Locale.forLanguageTag("Asia/Shanghai")).getTimeInMillis();//1573911004
        long timeInMillis = utcTime.getMillis();
        return timeInMillis;

    }

}
