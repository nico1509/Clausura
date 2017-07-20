package de.nico_assfalg.apps.android.clausura.time;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Calendar;

import de.nico_assfalg.apps.android.clausura.R;

import static java.lang.Math.abs;

public abstract class Calculator {

    public static int daysUntil(Date date) {
        LocalDate now = new LocalDate()
                .toDateTimeAtStartOfDay().toLocalDate();
        LocalDate then = new LocalDate(date.getYear(), date.getMonth(), date.getDay())
                .toDateTimeAtStartOfDay().toLocalDate();
        Days daysUntil = Days.daysBetween(now, then);
        return daysUntil.getDays();
    }

    public static String daysUntilAsString(Date date, Context c) {
        int daysUntil = daysUntil(date);
        String until;
        if (daysUntil == 1) {
            until = c.getString(R.string.text_tomorrow);
        } else if (daysUntil == 0) {
            until = c.getString(R.string.text_today);
        } else if (daysUntil < 0) {
            until = c.getString(R.string.text_yesterday);
            if (daysUntil < -1) {
                until = c.getString(R.string.text_x_days_ago, abs(daysUntil));
            }
        }
        else {
            until = c.getString(R.string.text_in_x_days, daysUntil);
        }
        return until;
    }
}
