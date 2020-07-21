package com.erastus.orientate.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateUtils {

    public static LocalDateTime localDateTimeFromDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date dateFromLocalTime(LocalDateTime localDateTime) {
        return Date.from( localDateTime.atZone( ZoneId.systemDefault()).toInstant());
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = Objects.requireNonNull(sf.parse(rawJsonDate)).getTime();
            relativeDate = android.text.format.DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getRelativeTimeAgo(LocalDateTime localDateTime) {
        String relativeDate = "";

        long dateMillis = localDateTime.getNano() / 1000L;
        relativeDate = android.text.format.DateUtils.getRelativeTimeSpanString(dateMillis,
                System.currentTimeMillis(), android.text.format.DateUtils.SECOND_IN_MILLIS).toString();

        return relativeDate;
    }

    public static String getTimeAmPm(LocalDateTime time) {
        //Displaying current time in 12 hour format with AM/PM
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        return dateFormat.format(dateFromLocalTime(time));
    }

    public static String getTimeWithYearAmPm(LocalDateTime time) {
        //Displaying current date and time in 12 hour format with AM/PM
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault());
        return dateFormat.format(dateFromLocalTime(time));
    }
}
