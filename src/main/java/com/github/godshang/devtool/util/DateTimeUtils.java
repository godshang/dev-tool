package com.github.godshang.devtool.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);

    public static LocalDateTime parse(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static String format(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DEFAULT_DATE_TIME_FORMATTER);
    }

    public static ZoneId getSystemZone() {
        return ZoneId.systemDefault();
    }

    public static LocalDateTime getDateTime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, getSystemZone());
    }

    public static String getDateTimeStr(long timestamp) {
        LocalDateTime localDateTime = getDateTime(timestamp);
        return format(localDateTime);
    }

    public static long getTimestamp(String dateTimeStr) {
        LocalDateTime localDateTime = parse(dateTimeStr);
        return getTimestamp(localDateTime);
    }

    public static long getTimestamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(getSystemZone()).toInstant().toEpochMilli();
    }

    public static Duration between(LocalDateTime from, LocalDateTime to) {
        return Duration.between(from, to);
    }

    public static long daysBetween(LocalDateTime from, LocalDateTime to) {
        return between(from, to).toDays();
    }
}
