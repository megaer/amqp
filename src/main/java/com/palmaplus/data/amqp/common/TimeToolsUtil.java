package com.palmaplus.data.amqp.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by jiabing.zhu on 2016/10/17.
 */
public class TimeToolsUtil {
    /**
     * epoch millisecond to String with default Zone (Asia/Shanghai) and default pattern (yyyy-MM-dd HH:mm:ss)
     * example: epochMilli = 1476428688829,  result = 2016-10-14 15:04:48
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli){
        String defaultPattern = "yyyy-MM-dd HH:mm:ss";
        return epochMilliToString(epochMilli, defaultPattern);
    }

    /**
     * epoch millisecond to String with default Zone (Asia/Shanghai) and pattern
     * example: epochMilli = 1476428688829, pattern = "yyyy-MM-dd HH:mm:ss",  result = 2016-10-14 15:04:48
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli, String pattern){
        Instant instant = Instant.ofEpochMilli(epochMilli);
        ZoneId defaultZoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
        return epochMilliToString(epochMilli, defaultZoneId, pattern);
    }

    /**
     * epoch millisecond to String with default Zone (Asia/Shanghai) and DateTimeformatter
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli, DateTimeFormatter formatter){
        Instant instant = Instant.ofEpochMilli(epochMilli);
        ZoneId defaultZoneId = ZoneId.of(ZoneId.SHORT_IDS.get("CTT"));
        return epochMilliToString(epochMilli, defaultZoneId, formatter);
    }

    /**
     * epoch millisecond to String with Zone and default pattern (yyyy-MM-dd HH:mm:ss)
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli, ZoneId zoneId){
        String defaultPattern = "yyyy-MM-dd HH:mm:ss";
        return epochMilliToString(epochMilli, zoneId, defaultPattern);
    }

    /**
     * epoch millisecond to String with Zone and pattern
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli, ZoneId zoneId, String pattern){
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, zoneId)
                .format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * epoch millisecond to String with Zone and DateTimeFormatter
     * @param epochMilli
     * @return
     */
    public static String epochMilliToString(long epochMilli, ZoneId zoneId, DateTimeFormatter formatter){
        Instant instant = Instant.ofEpochMilli(epochMilli);
        return LocalDateTime.ofInstant(instant, zoneId)
                .format(formatter);
    }
    /**
     * get current date eith pattern yyyy-MM-dd HH:mm:ss
     * @return String
     */
    public static String currrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    /**
     * epoch date String to millisecind
     * for example: date String (yyyy-MM-mm HH:mm:ss) --> '147896522325'
     * @param dateStr
     * @return long
     */
    public static long epochDateStrToMill(String dateStr,String pattern) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(dateStr).getTime();
    }

/*    public static void main(String[] args) throws ParseException{
        System.out.println(epochDateStrToMill(currrentTime(),"yyyy-MM-dd HH:mm:ss"));
    }*/
}
