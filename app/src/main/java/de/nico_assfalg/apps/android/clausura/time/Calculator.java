package de.nico_assfalg.apps.android.clausura.time;

import android.content.Context;

import java.util.Calendar;

import de.nico_assfalg.apps.android.clausura.R;

import static java.lang.Math.abs;

public abstract class Calculator {

    public static int daysUntil(Date date) {
        Date current = new Date(Calendar.getInstance());
        long distance = date.toMs() - current.toMs();
        return (int) (distance / 1000 / 86400); //convert ms to days
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
