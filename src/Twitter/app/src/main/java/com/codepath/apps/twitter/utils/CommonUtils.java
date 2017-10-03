package com.codepath.apps.twitter.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;
import android.view.View;

import com.codepath.apps.twitter.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rdeshpan on 9/27/2017.
 */

public class CommonUtils {

    private static final String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    public static String getRelativeTimeAgo(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            //relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
            return getDateDifferenceForDisplay(sf.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    // Ref: https://stackoverflow.com/questions/19409035/custom-format-for-relative-time-span
    private static String getDateDifferenceForDisplay(Date inputdate) {
        Calendar now = Calendar.getInstance();
        Calendar then = Calendar.getInstance();

        now.setTime(new Date());
        then.setTime(inputdate);

        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = then.getTimeInMillis();

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffSeconds < 60) {
            return diffSeconds + "s";

        } else if (diffMinutes < 60) {
            return diffMinutes + "m";

        } else if (diffHours < 24) {
            return diffHours + "h";

        } else if (diffDays < 7) {
            return diffDays + "d";

        } else {
            SimpleDateFormat todate = new SimpleDateFormat("MMM dd",
                    Locale.ENGLISH);

            return todate.format(inputdate);
        }
    }

    public static String getExactDate(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa . MMM dd yyyy");

        String date = "";
        try {
            date = output.format(sf.parse(rawJsonDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static void showMessage(View view , String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

    }
}
