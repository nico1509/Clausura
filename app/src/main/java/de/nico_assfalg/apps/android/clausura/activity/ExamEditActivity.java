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
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import de.nico_assfalg.apps.android.clausura.time.Date;
import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;
import de.nico_assfalg.apps.android.clausura.R;

public class ExamEditActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static String examTitle;
    private static String examDate;
    private static String examTime;
    private static String examLocation;
    private static String examNotes;
    private static int examProgress;

    int id;

    ExamDBHelper dbHelper;
    Cursor rs;

    Toolbar titleToolbar;

    EditText editTitle;
    EditText editDate;
    EditText editTime;
    EditText editLocation;
    EditText editNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //EditText in Title
        titleToolbar = (Toolbar) findViewById(R.id.titleToolbar);

        dbHelper = new ExamDBHelper(this);

        Intent intent = getIntent();
        id = intent.getIntExtra(MainActivity.KEY_EXTRA_EXAM_ID, 0);

        if (id > 0) {
            rs = dbHelper.getExam(id);
            rs.moveToFirst();
        }

        initializeStrings();
        initializeEditTexts();
        setTextOfEditTexts();
    }

    private void initializeEditTexts() {
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout tempParent = (LinearLayout) inflater.inflate(R.layout.layout_edittext_toolbar, null);
        editTitle = (EditText) tempParent.findViewById(R.id.toolbarEditText);
        tempParent.removeAllViews();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editTitle.setHintTextColor(getColor(R.color.colorWhiteAlpha));
        } else {
            editTitle.setHintTextColor(getResources().getColor(R.color.colorWhiteAlpha));
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            titleToolbar.setVisibility(View.VISIBLE);
            titleToolbar.addView(editTitle);
        } else {
            titleToolbar.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(editTitle);
        }

        editDate = (EditText) findViewById(R.id.editDate);
        editDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    saveStrings();
                    DialogFragment df = new DatePickerFragment();
                    df.show(getFragmentManager(), "datePicker");
                }
                return true;
            }
        });
        editDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    saveStrings();
                    DialogFragment df = new DatePickerFragment();
                    df.show(getFragmentManager(), "datePicker");
                }
            }
        });
        editTime = (EditText) findViewById(R.id.editTime);
        editTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    saveStrings();
                    DialogFragment df = new TimePickerFragment();
                    df.show(getFragmentManager(), "timePicker");
                }
                return true;
            }
        });
        editTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    saveStrings();
                    DialogFragment df = new TimePickerFragment();
                    df.show(getFragmentManager(), "timePicker");
                }
            }
        });
        editLocation = (EditText) findViewById(R.id.editLocation);
        editNotes = (EditText) findViewById(R.id.editNotes);
    }

    private void setTextOfEditTexts() {
        editTitle.setText(examTitle);
        if (!examDate.equals("")) {
            Date tempDate = new Date(examDate);
            editDate.setText(tempDate.toHumanString());
        }
        if (!examTime.equals("")) {
            editTime.setText(Date.parseTimeStringToHumanString(examTime));
        }
        editLocation.setText(examLocation);
        editNotes.setText(examNotes);
    }

    private void initializeStrings() {
        if (id > 0) {
            examTitle = rs.getString(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TITLE));
            examDate = rs.getString(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE));
            examTime = rs.getString(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TIME));
            examLocation = rs.getString(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_LOCATION));
            examNotes = rs.getString(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_NOTES));
            examProgress = rs.getInt(rs.getColumnIndex(ExamDBHelper.EXAM_COLUMN_PROGRESS));
        } else {
            examTitle = "";
            examDate = "";
            examTime = "";
            examLocation = "";
            examNotes = "";
            examProgress = 0;
        }
    }

    protected void saveStrings() {
        examTitle = editTitle.getText().toString();
        examLocation = editLocation.getText().toString();
        examNotes = editNotes.getText().toString();
    }

    public static int[] datePickerValues() {
        if (examDate.equals("")) {
            final Calendar c = Calendar.getInstance();
            int[] values = {c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)};
            return values;
        } else {
            String[] dateParts = examDate.split("-");
            int[] values = {Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2])};
            return values;
        }
    }

    public static int[] timePickerValues() {
        if (examTime.equals("")) {
            final Calendar c = Calendar.getInstance();
            int[] values = {c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE)};
            return values;
        } else {
            String[] timeParts = examTime.split("-");
            int[] values = {Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1])};
            return values;
        }
    }

    public static class DatePickerFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int[] yearMonthDay = datePickerValues();
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(),
                    yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String monthString = String.valueOf(month + 1);
        String dayString = String.valueOf(dayOfMonth);
        if (monthString.length() < 2) {
            monthString = "0" + monthString;
        }
        if (dayString.length() < 2) {
            dayString = "0" + dayString;
        }
        examDate = year + "-" + monthString + "-" + dayString;
        setTextOfEditTexts();
    }

    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int[] hourMinute = timePickerValues();
            return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(),
                    hourMinute[0], hourMinute[1], DateFormat.is24HourFormat(getActivity()));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hourString = String.valueOf(hourOfDay);
        String minuteString = String.valueOf(minute);
        if (hourString.length() < 2) {
            hourString = "0" + hourString;
        }
        if (minuteString.length() < 2) {
            minuteString = "0" + minuteString;
        }
        examTime = hourString + "-" + minuteString;
        setTextOfEditTexts();
    }

    public void persistExam() {
        if (id > 0) {
            if (dbHelper.updateExam(id, examTitle, examDate, examTime, examLocation, examNotes, examProgress)) {
                //Toast.makeText(getApplicationContext(), "Klausur geändert!", Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Klausur ändern fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (examTitle.equals("") || examDate.equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    editTitle.setHintTextColor(getColor(R.color.colorOrange));
                    editDate.setHintTextColor(getColor(R.color.colorOrange));
                } else {
                    editTitle.setHintTextColor(getResources().getColor(R.color.colorOrange));
                    editDate.setHintTextColor(getResources().getColor(R.color.colorOrange));
                }
                Snackbar snackbar = Snackbar.make(editTitle, "Es müssen mindestens Titel und Datum eingetragen sein!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                if (dbHelper.insertExam(examTitle, examDate, examTime, examLocation, examNotes, examProgress)) {
                    //Toast.makeText(getApplicationContext(), "Klausur hinzugefügt!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Klausur hinzufügen fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                }
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(backIntent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveStrings();
            persistExam();
            return true;
        }
        return false;
    }
}
