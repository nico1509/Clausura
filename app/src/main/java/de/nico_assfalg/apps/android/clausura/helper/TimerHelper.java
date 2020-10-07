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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.browser.customtabs.CustomTabsIntent;

import java.net.URL;

import de.nico_assfalg.apps.android.clausura.R;

public class TimerHelper {

    public static final String TIMER_ACTION = "de.nico_assfalg.apps.android.clausura.TIMER_ACTION";
    public static final String TIMER_PACKAGE = "org.secuso.privacyfriendlyproductivitytimer";
    public static final String DOWNLOAD_URL = "https://nico-assfalg.de/Clausura/#Timer";

    private Context context;

    public TimerHelper(Context context) {
        this.context = context;
    }

    public boolean isTimerAppInstalled() {
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(TIMER_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public Intent buildTimerIntent(int examId, String examTitle, String examDescription, int examProgress) {
        Intent intent = new Intent(TIMER_ACTION);
        intent.putExtra("todo_id", examId)
                .putExtra("todo_name", examTitle)
                .putExtra("todo_description", examDescription)
                .putExtra("todo_progress", 1)
                .setPackage(TIMER_PACKAGE)
                .setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        return intent;
    }

    public Intent buildDownloadIntent() {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setToolbarColor(context.getColor(R.color.colorPrimary));
        }
        CustomTabsIntent intent = builder.build();
        return intent.intent.setData(Uri.parse(DOWNLOAD_URL));
    }
}
