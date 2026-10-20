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
package de.nico_assfalg.apps.android.clausura.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.Fragment;
import android.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import de.nico_assfalg.apps.android.clausura.fragment.MainFragment;
import de.nico_assfalg.apps.android.clausura.time.Calculator;
import de.nico_assfalg.apps.android.clausura.time.Date;
import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;
import de.nico_assfalg.apps.android.clausura.helper.PreferenceHelper;
import de.nico_assfalg.apps.android.clausura.R;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    public static final String KEY_EXTRA_EXAM_ID = "KEY_EXTRA_EXAM_ID";

    private static final int FRAGMENT_MAIN = 1;

    private LinearLayout examList;
    ExamDBHelper dbHelper;

    private int allExamCounter;
    private int pastExamCounter;

    static String lectureEndDate;
    private LinearLayout lectureEndLayout;

    final String SEPARATOR = "  ·  ";

    Date tempDate; //needed for monthYear labels

    Fragment mainFragment;

    //OLD SHIT
    private static final int PERMISSION_REQUEST_ID_EXTERNAL_STORAGE = 42;
    private static final int BACKUP_SAVE = 0;
    private static final int BACKUP_RESTORE = 1;
    private static int backupMode;
    //

    /*

                ACTIVITY

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ImageView in ActionBar
        getSupportActionBar().setIcon(R.drawable.clausura_transparent_schmal_klein);
        getSupportActionBar().setTitle("");

        //Initialize FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExamEditActivity.class);
                intent.putExtra(KEY_EXTRA_EXAM_ID, 0);
                startActivity(intent);
            }
        });

        PreferenceHelper.importOldSettings(getApplicationContext());

        initLectureEnd();
    }

    private void showFragment () {
        int fragment;
        String fragmentToLoad = PreferenceHelper.getPreference(this, PreferenceHelper.FRAGMENT_TO_LOAD);
        if (fragmentToLoad.equals("")) {
            fragment = FRAGMENT_MAIN;
        } else {
            fragment = Integer.parseInt(fragmentToLoad);
        }
        switch (fragment) {
            case FRAGMENT_MAIN:
                mainFragment = new MainFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentContainerMain, mainFragment);
                transaction.addToBackStack("main_fragment");
                transaction.commit();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLectureEnd();
        showFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initLectureEnd();
        showFragment();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /*

                LAYOUT

     */

    private void initLectureEnd() {
        lectureEndLayout = (LinearLayout) findViewById(R.id.lectureEndLayout);
        View listDivider = (View) findViewById(R.id.listDividerLectureEnd);
        final LinearLayout lectureEnd = (LinearLayout) lectureEndLayout.findViewById(R.id.examLayout);
        ImageButton showHideButton = (ImageButton) lectureEndLayout.findViewById(R.id.showHideLectureEndButton);
        if (PreferenceHelper.getPreference(this, "showLectureEnd").equals("0")) {
            lectureEnd.setVisibility(View.GONE);
            listDivider.setVisibility(View.GONE);
            showHideButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
        } else {
            lectureEnd.setVisibility(View.VISIBLE);
            //listDivider.setVisibility(View.VISIBLE);
            showHideButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
        }
        lectureEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment df = new DatePickerFragment();
                df.show(getFragmentManager(), "datePicker");
            }
        });
        TextView titleText = (TextView) lectureEnd.findViewById(R.id.examTitle);
        String text = PreferenceHelper.getPreference(getApplicationContext(),
                PreferenceHelper.PINNED_DATE_TITLE);
        if (text.equals("")) {
            text = getString(R.string.text_end_of_lecture);
            PreferenceHelper.setPreference(this, text,
                    PreferenceHelper.PINNED_DATE_TITLE);
        }
        titleText.setText(text);
        TextView daysUntil = (TextView) lectureEnd.findViewById(R.id.examDaysUntil);
        lectureEndDate = PreferenceHelper.getPreference(this, "endOfLectureDate");
        if (lectureEndDate.equals("")) {
            Date current = new Date(Calendar.getInstance());
            lectureEndDate = current.toString();
            PreferenceHelper.setPreference(this, lectureEndDate, "endOfLectureDate");
        }
        Date date = new Date(lectureEndDate);
        String dateAndUntil = date.toHumanString() + SEPARATOR + Calculator.daysUntilAsString(date, this);
        daysUntil.setText(dateAndUntil);
        TextView invisibleDate = (TextView) lectureEnd.findViewById(R.id.examDay);
        invisibleDate.setVisibility(View.INVISIBLE);
        TextView invisibleDay = (TextView) lectureEnd.findViewById(R.id.examDayOfWeek);
        invisibleDay.setVisibility(View.INVISIBLE);
        showHideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton imgBtn = (ImageButton) view;
                if (lectureEnd.getVisibility() != View.GONE) {
                    PreferenceHelper.setPreference(getApplicationContext(), "0", "showLectureEnd");
                    lectureEnd.setVisibility(View.GONE);
                    imgBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
                } else {
                    PreferenceHelper.setPreference(getApplicationContext(), "1", "showLectureEnd");
                    lectureEnd.setVisibility(View.VISIBLE);
                    imgBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
                }

            }
        });
    }

    /*

                OLD SHIT

     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Affects the content of the options menu everytime it's displayed
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement  //TODO: Ein kleines Fensterchen machen
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_backup) {
            Intent intent = new Intent(MainActivity.this, BackupRestoreActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //DatePicker Shit

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String[] dateParts = lectureEndDate.split("-");
            int[] yearMonthDay = {Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1])-1, Integer.parseInt(dateParts[2])};
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(),
                    yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        month++;
        PreferenceHelper.setPreference(this, year + "-" + month + "-" + dayOfMonth, "endOfLectureDate");
        initLectureEnd();
    }
}
