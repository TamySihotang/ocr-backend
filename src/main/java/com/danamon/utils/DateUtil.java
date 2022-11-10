package com.danamon.utils;

import com.danamon.exception.ApplicationException;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    static final String DEFAULT_FORMAT = "yyyy-MMM-dd";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PATTERN_2 = "yyyy-MM-dd HH";
    public static final String DATE_TIME_PATTERN_3 = "yyyyMMddHHmm";
    public static final String DATE_TIME_PATTERN_4 = "yyyyMMddHHmmss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATE_PATTERN2 = "dd-MM-yyyy";
    public static final String DATE_DAY_PATTERN = "E, yyyy-MM-dd";
    public static final String TIME_PATTERN = "hh:mm:ss";
    public static final String TIME_PATTERN_2 = "HH:mm";
    public static final String TIME_PATTERN_3 = "mm";
    public static final String DATE_PATTERN_2 = "dd-MMM-yyyy";
    public static final String DATE_PATTERN_3 = "dd/MM/yyyy";
    public static final String DATE_PATTERN_4 = "MM/dd/yyyy";
    public static final String DATE_PATTERN_5 = "yyyyMMdd";
    public static final String DATE_PATTERN_6 = "MMM-yy";
    public static final String DATE_PATTERN_7 = "ddMMyyyy";
    public static final String DATE_PATTERN_8 = "dd-MM-yyyy";
    public static final String DATE_PATTERN_9 = "ddMMMyyyy";
    public static final String DATE_PATTERN_10 = "dd-MM-yyy HH:mm:ss";
    public static final String DATE_PATTERN_11 = "dd-MM-yyy-HHmmss";
    public static final String DATE_PATTERN_12 = "MMMM yyyy";

    private DateUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static Calendar newCal() {
        return Calendar.getInstance();
    }

    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    public static Date addTimeHour(Date time, Integer value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.HOUR_OF_DAY, value);
        return cal.getTime();
    }

    public static Date addTimeMinute(Date time, Integer value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.MINUTE, value);
        return cal.getTime();
    }

    public static Date setDayOfMonth(Date date, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return cal.getTime();
    }

    public static String formatDateToString(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return null;
    }




    public static Date stringToDate(String date) {

        Date result;
        DateFormat dateFormat = new SimpleDateFormat(DEFAULT_FORMAT);

        try {
            result = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new ApplicationException("Format Date " + DEFAULT_FORMAT + " ex: " + dateToString(new Date()));
        }

        return result;

    }

    public static Date stringToDate(String date, String format) {

        Date result;
        DateFormat dateFormat = new SimpleDateFormat(format);

        try {
            result = dateFormat.parse(date);
        } catch (ParseException e) {
            throw new ApplicationException("Format Date " + DEFAULT_FORMAT + " ex: " + dateToString(new Date()));
        }

        return result;

    }

    public static Date stringPeriodeToDate(String periode, String format){
        Locale localeIndonesia = new Locale("id", "ID");
        periode = periode.substring(0,1).toUpperCase() + periode.substring(1).toLowerCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, localeIndonesia);
        YearMonth yearMonth = formatter.parse(periode, YearMonth::from);
        LocalDate date = yearMonth.atDay(1);
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static String dateToString(Date date) {
        return dateToString(date, DATE_PATTERN2);
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }


}