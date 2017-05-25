package de.nico_assfalg.apps.android.clausura;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class PreferenceHelper {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static final String IMPORT_SUCCESSFUL = "importSuccess";

    /*
    Saving Strings to SharedPreferences
     */
    public static void setPreference(Context c, String value, String key) {
        settings = PreferenceManager.getDefaultSharedPreferences(c);
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /*
    Getting Strings from SharedPreferences
     */
    public static String getPreference(Context c, String key) {
        settings = PreferenceManager.getDefaultSharedPreferences(c);
        String value = settings.getString(key, "");
        return value;
    }

    public static void importOldSettings(Context context) {
        if (getPreference(context, IMPORT_SUCCESSFUL).equals("")) {
            String data = getPreference(context, "examList");
            if (!data.equals("")) {
                String[] exams = data.split(" ");
                ExamDBHelper dbHelper = new ExamDBHelper(context);
                int i = 0;
                while (i < exams.length) {
                    String title = exams[i+1];
                    String date = parseOldDate(exams[i]);
                    i += 4;
                    dbHelper.insertExam(title, date, "", "", "");
                }
            }
            setPreference(context, "true", IMPORT_SUCCESSFUL);
        }
    }

    private static String parseOldDate(String oldDate) {
        String[] parts = oldDate.split("-"); //0: day, 1: month, 2: year
        String day;
        String month;
        String year;

        if (parts[0].length() == 1) {
            day = "0" + parts[0];
        } else {
            day = parts[0];
        }

        if (parts[1].length() == 1) {
            month = "0" + parts[1];
        } else {
            month = parts[1];
        }

        year = parts[2];

        return year + "-" + month + "-" + day;
    }



}
