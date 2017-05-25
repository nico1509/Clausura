package de.nico_assfalg.apps.android.clausura;

import java.util.Calendar;

/**
 * @version 1.2
 */

public class Date {

    private int day;
    private int month;
    private int year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Date(Calendar calendar) {
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.month = calendar.get(Calendar.MONTH ) + 1;
        this.year = calendar.get(Calendar.YEAR);
    }

    public Date(String dateString) {
        int[] dateParts = new int[3];
        int i = 0;
        for (String part : dateString.split("-")) {
            dateParts[i] = Integer.parseInt(part);
            i++;
        }
        this.day = dateParts[2];
        this.month = dateParts[1];
        this.year = dateParts[0];
    }

    public long toMs() {
        //Initialize
        Calendar c = Calendar.getInstance();
        //Set
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        //Get
        return c.getTimeInMillis();
    }

    public String toString() {
        return year + "-" + month + "-" + day;
    }

    public String toHumanString() {

        String textMonth;

        switch (month) {
            case 1:
                textMonth = "Januar";
                break;
            case 2:
                textMonth = "Februar";
                break;
            case 3:
                textMonth = "März";
                break;
            case 4:
                textMonth = "April";
                break;
            case 5:
                textMonth = "Mai";
                break;
            case 6:
                textMonth = "Juni";
                break;
            case 7:
                textMonth = "Juli";
                break;
            case 8:
                textMonth = "August";
                break;
            case 9:
                textMonth = "September";
                break;
            case 10:
                textMonth = "Oktober";
                break;
            case 11:
                textMonth = "November";
                break;
            case 12:
                textMonth = "Dezember";
                break;

            default:
                textMonth = "" + month;
        }

        return day + ". " + textMonth + " " + year;
    }

    public static String parseTimeStringToHumanString(String time) {
        String[] parts = time.split("-");
        return parts[0] + ":" + parts[1];
    }

    @Override
    public boolean equals(Object obj) {
        Date compare = (Date) obj;
        if (toMs() == compare.toMs()) {
            return true;
        }
        return false;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getDayAsShortString() {
        //Initialize
        Calendar c = Calendar.getInstance();
        //Set
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 0);
        //Get
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        switch (dayOfWeek) {
            case Calendar.MONDAY: return "Mo.";
            case Calendar.TUESDAY: return "Di.";
            case Calendar.WEDNESDAY: return "Mi.";
            case Calendar.THURSDAY: return "Do.";
            case Calendar.FRIDAY: return "Fr.";
            case Calendar.SATURDAY: return "Sa.";
            case Calendar.SUNDAY: return "So.";
            default: return "Date Error";
        }
    }

    public String getMonthAsString() {
        switch (getMonth()) {
            case 1: return "Januar";
            case 2: return "Februar";
            case 3: return "März";
            case 4: return "April";
            case 5: return "Mai";
            case 6: return "Juni";
            case 7: return "Juli";
            case 8: return "August";
            case 9: return "September";
            case 10: return "Oktober";
            case 11: return "November";
            case 12: return "Dezember";
            default: return "Date Error";
        }
    }

}
