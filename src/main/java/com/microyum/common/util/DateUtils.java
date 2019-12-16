package com.microyum.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

/**
 * Author: Rison.Gao
 * TIME: 2017-09-29 17:12
 * Version: 1.0
 */
public class DateUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_US = "MM/dd/yyyy";
    public static final String DATE_FORMAT_US2 = "MM-dd-yyyy";
    public static final String DATE_FORMAT_COMP = "yyyyMMdd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FULL_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_COMP_YYMMDD = "yyyyMMdd";
    public static final String YYMMddHHmmss = "yyyyMMddHHmmss";
    public static final String DATE_TIME_FORMAT2 = "MM-dd-yyyy HH:mm:ss";

    public static final String DATE_TIME_FORMAT_OP = "yyyy-MM-dd HH:mm";

    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String getCurrentDateTime() {
        return formatDate(new Date(), DATE_TIME_FORMAT);
    }

    public static String getCurrentDate() {
        return formatDate(new Date(), DATE_FORMAT);
    }

    public static long getDateDiff(Date start, Date end, TimeUnit timeUnit) {
        long diff = end.getTime() - start.getTime();
        return timeUnit.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date getStartOfDay(Date date) {
        if (null == date) return null;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfDay(Date date) {
        if (null == date) return null;
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.toLocalDate().atStartOfDay().plusSeconds(86399);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date getUtcDatetime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis() - java.util.TimeZone.getDefault().getRawOffset());
        return cal.getTime();
    }

    public static Date getSpecificTime(Date date, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, calendar.getMinimum(Calendar.MILLISECOND));
        return calendar.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static String today() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat(YYMMddHHmmss).format(cal.getTime());
    }

    public static Date parseDate(final String dateValue, final String dateFormat) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(dateValue, new String[]{dateFormat});
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date getStartOfWeekDay(Date date) {
        if (date == null)
            return null;
        LocalDate now = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(now.with(previousOrSame(MONDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfWeekDay(Date date) {
        if (date == null)
            return null;
        LocalDate now = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(now.with(nextOrSame(SUNDAY)).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getStartOfMonthDay(Date date) {
        if (date == null)
            return null;
        LocalDate now = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static Date getEndOfMonthDay(Date date) {
        if (date == null)
            return null;
        LocalDate now = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Date.from(now.withDayOfMonth(now.lengthOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static boolean isSameDay(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        final Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
