package de.nico_assfalg.apps.android.clausura;

import java.util.Calendar;

import static java.lang.Math.abs;

public abstract class Calculator {

    public static int daysUntil(Date date) {
        Date current = new Date(Calendar.getInstance());
        long distance = date.toMs() - current.toMs();
        return (int) (distance / 1000 / 86400); //convert ms to days
    }

    public static String daysUntilAsString(Date date) {
        int daysUntil = daysUntil(date);
        String until;
        if (daysUntil == 1) {
            until = "Morgen";
        } else if (daysUntil == 0) {
            until = "Heute";
        } else if (daysUntil < 0) {
            until = "Gestern";
            if (daysUntil < -1) {
                until = "Vor " + abs(daysUntil) + " Tagen";
            }
        }
        else {
            until = "In " + daysUntil + " Tagen";
        }
        return until;
    }
}
