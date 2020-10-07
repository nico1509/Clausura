/*
    Copyright 2020 Nico Aßfalg

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package de.nico_assfalg.apps.android.clausura.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;

public abstract class PreferenceHelper {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static final String IMPORT_SUCCESSFUL = "importSuccess";
    public static final String PINNED_DATE_TITLE = "pinnedDateTitle";
    public static final String FRAGMENT_TO_LOAD = "fragmentToLoad";
    public static final String UPDATE_WIFI_ONLY = "updateWifiOnly";

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

    public static String getAppVersion(Context context) {
        PackageInfo pInfo;
        String version = "ERROR";
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }



}
