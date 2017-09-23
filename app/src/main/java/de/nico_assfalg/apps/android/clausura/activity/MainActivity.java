package de.nico_assfalg.apps.android.clausura.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import de.nico_assfalg.apps.android.clausura.fragment.MainFragment;
import de.nico_assfalg.apps.android.clausura.time.Calculator;
import de.nico_assfalg.apps.android.clausura.time.Date;
import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;
import de.nico_assfalg.apps.android.clausura.helper.PreferenceHelper;
import de.nico_assfalg.apps.android.clausura.R;

import static de.nico_assfalg.apps.android.clausura.R.id.coordinatorLayout;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {

    public static final String KEY_EXTRA_EXAM_ID = "KEY_EXTRA_EXAM_ID";

    private LinearLayout examList;
    ExamDBHelper dbHelper;

    private int allExamCounter;
    private int pastExamCounter;

    static String lectureEndDate;
    private LinearLayout lectureEndLayout;

    final String SEPARATOR = "  ·  ";

    Date tempDate; //needed for monthYear labels

    ConstraintLayout updateCard;

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

        mainFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainerMain, mainFragment);
        transaction.commit();

        //checkForUpdate(); TODO: Re-implement
    }

    @Override
    protected void onResume() {
        super.onResume();
        examList = (LinearLayout) findViewById(R.id.examList);
        examList.removeAllViews();
        populate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        examList = (LinearLayout) findViewById(R.id.examList);
        examList.removeAllViews();
        populate();
    }

    /*

                LAYOUT

     */

    private void populate() {

        tempDate = new Date(0,0,0);

        dbHelper = new ExamDBHelper(this);

        final Cursor cursor = dbHelper.getAllExams();

        if (cursor.moveToFirst()) {
            allExamCounter = 0;
            pastExamCounter = 0;
            while (!cursor.isAfterLast()) {
                //Get exam Title, Date and _id
                String title = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE));
                String time = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TIME));
                int id = cursor.getInt(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_ID));

                if (!pastAllowed()) {
                    Date exDate = new Date(date);
                    Date now = new Date(Calendar.getInstance());
                    if (now.toMs() <= exDate.toMs()) {
                        //Add Month/Year label if necessary
                        addMonthYearLabel(date);
                        //Make Exam Element and add it to the List
                        examList.addView(examElement(title, date, time, id));
                    } else {
                        pastExamCounter++;
                    }
                } else {
                    //Add Month/Year label if necessary
                    addMonthYearLabel(date);
                    //Make Exam Element and add it to the List
                    examList.addView(examElement(title, date, time, id));
                }
                allExamCounter++;
                cursor.moveToNext();
            }
        }
        cursor.close(); //Important!

        addNoExamText();

        /*Space downMargin = new Space(this);
        downMargin.setMinimumHeight(80);
        examList.addView(downMargin);*/
    }

    private void addNoExamText() {
        if (allExamCounter < 1) {
            LinearLayout ll = (LinearLayout) getLayoutInflater()
                    .inflate(R.layout.layout_no_exam_text, null);

            TextView noExams = (TextView) ll.findViewById(R.id.noExamText);
            noExams.setText(getString(R.string.text_no_exams));

            Button showPastButton = (Button) ll.findViewById(R.id.showPastButton);
            showPastButton.setVisibility(View.GONE);

            examList.addView(ll);
        } else {
            if (pastExamCounter >= allExamCounter && allExamCounter > 0) {
                LinearLayout ll = (LinearLayout) getLayoutInflater()
                        .inflate(R.layout.layout_no_exam_text, null);

                TextView noExams = (TextView) ll.findViewById(R.id.noExamText);
                noExams.setText(getString(R.string.text_no_exams_past));

                Button showPastButton = (Button) ll.findViewById(R.id.showPastButton);
                showPastButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPast(true);
                    }
                });
                if (pastExamCounter == 1) {
                    showPastButton.setText(getString(R.string.button_show_past_singular));
                } else {
                    showPastButton.setText(getString(R.string.button_show_past_plural, pastExamCounter));
                }

                examList.addView(ll);
            }
        }
    }

    private void initLectureEnd() {
        lectureEndLayout = (LinearLayout) findViewById(R.id.lectureEndLayout);
        final LinearLayout lectureEnd = (LinearLayout) lectureEndLayout.findViewById(R.id.examLayout);
        ImageButton showHideButton = (ImageButton) lectureEndLayout.findViewById(R.id.showHideLectureEndButton);
        if (PreferenceHelper.getPreference(this, "showLectureEnd").equals("0")) {
            lectureEnd.setVisibility(View.GONE);
            showHideButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
        } else {
            lectureEnd.setVisibility(View.VISIBLE);
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

    private LinearLayout examElement(String title, String dateString, String timeString, int id) {
        //inflate the layout file and define its TextViews
        final LinearLayout examLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_exam, null, false);
        TextView examDay = (TextView) examLayout.findViewById(R.id.examDay);
        TextView examDayOfWeek = (TextView) examLayout.findViewById(R.id.examDayOfWeek);
        TextView examTitle = (TextView) examLayout.findViewById(R.id.examTitle);
        TextView examDaysUntil = (TextView) examLayout.findViewById(R.id.examDaysUntil);

        //Define what happens when exam is clicked
        examLayout.setId(id);
        examLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExamDetailsDialog(v);
            }
        });

        //create a Date object with current exam date
        Date date = new Date(dateString);

        //set text of TextViews
        examDay.setText(String.valueOf(date.getDay()));
        examDayOfWeek.setText(date.getDayAsShortString());

        examTitle.setText(title);
        String examDaysUntilString = Calculator.daysUntilAsString(date, this);
        examDaysUntilString = !timeString.equals("") ?
                examDaysUntilString + SEPARATOR + Date.parseTimeStringToHumanString(timeString) :
                examDaysUntilString;

        examDaysUntil.setText(examDaysUntilString);

        return examLayout;
    }

    private void addMonthYearLabel(String date) {
        Date currentDate = new Date(date);
        final LinearLayout monthYearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_month_year_label, null, false);
        if (tempDate.getMonth() < currentDate.getMonth() && tempDate.getYear() == currentDate.getYear()) {
            TextView monthYearText = (TextView) monthYearLayout.findViewById(R.id.monthYearText);
            String text = currentDate.getMonthAsString();
            monthYearText.setText(text);
            monthYearText.setTag(text);
            examList.addView(monthYearLayout);
        } else if (tempDate.getYear() < currentDate.getYear()) {
            TextView monthYearText = (TextView) monthYearLayout.findViewById(R.id.monthYearText);
            String text = currentDate.getMonthAsString() + " " + currentDate.getYear();
            monthYearText.setText(text);
            monthYearText.setTag(text);
            examList.addView(monthYearLayout);
        }
        tempDate = currentDate;


    }

    public void showExamDetailsDialog(final View v) {
        final Cursor cursor = dbHelper.getExam(v.getId());
        if (cursor.moveToFirst()) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.layout_dialog_exam_details);
            dialog.setTitle("Details");

            TextView titleLineText = (TextView) dialog.findViewById(R.id.titleLineText);
            titleLineText.setText(cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TITLE)));

            TextView dateLineTextDate = (TextView) dialog.findViewById(R.id.dateLineTextDate);
            TextView dateLineTextUntil = (TextView) dialog.findViewById(R.id.dateLineTextUntil);
            Date date = new Date(cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE)));
            dateLineTextDate.setText(date.toHumanString());
            dateLineTextUntil.setText(Calculator.daysUntilAsString(date, this));

            TextView timeLineText = (TextView) dialog.findViewById(R.id.timeLineText);
            String time = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TIME));
            if (time.equals("")) {
                dialog.findViewById(R.id.timeLine).setVisibility(View.GONE);
            } else {
                timeLineText.setText(Date.parseTimeStringToHumanString(time));
            }

            TextView locationLineText = (TextView) dialog.findViewById(R.id.locationLineText);
            String location = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_LOCATION));
            if (location.equals("")) {
                dialog.findViewById(R.id.locationLine).setVisibility(View.GONE);
            } else {
                locationLineText.setText(location);
            }

            TextView notesLineText = (TextView) dialog.findViewById(R.id.notesLineText);
            String notes = cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_NOTES));
            if (notes.equals("")) {
                dialog.findViewById(R.id.notesLine).setVisibility(View.GONE);
            } else {
                notesLineText.setText(notes);
            }

            Button examEditButton = (Button) dialog.findViewById(R.id.examEditButton);
            examEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    Intent intent = new Intent(MainActivity.this, ExamEditActivity.class);
                    intent.putExtra(KEY_EXTRA_EXAM_ID, v.getId()); //Start Editing Activity with id of exam to edit
                    startActivity(intent);
                }
            });

            Button examDeleteButton = (Button) dialog.findViewById(R.id.examDeleteButton);
            examDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteExam(v);
                    dialog.dismiss();
                }
            });
            cursor.close();

            //Set Width to match_parent
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.show();
        } else {
            cursor.close();
        }
    }

    private void deleteExam(View v) {
        Cursor examToDelete = dbHelper.getExam(v.getId());
        examToDelete.moveToFirst();
        Date examDate = new Date(examToDelete.getString(examToDelete
                .getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE)));
        int examMonth = examDate.getMonth();
        int examYear = examDate.getYear();
        Cursor cursor = dbHelper.getAllExams();

        int examIndex = examList.indexOfChild(v);

        dbHelper.deleteExam(v.getId());
        examList.removeView(v);

        allExamCounter--;
        if (new Date(Calendar.getInstance()).toMs() > examDate.toMs()) {
            pastExamCounter--;
        }

        boolean sameMonthInYear = false;
        boolean allInPast = !pastAllowed();

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext() && !sameMonthInYear) {
                Date date = new Date(cursor.getString(cursor.
                        getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE)));
                int month = date.getMonth();
                int year = date.getYear();

                sameMonthInYear = (month == examMonth) && (year == examYear);

                Date current = new Date(Calendar.getInstance());

                if (allInPast && month == current.getMonth()) {
                    allInPast = (month == examMonth) && (date.toMs() < current.toMs());
                }
            }
        }

        if (!sameMonthInYear || allInPast) {
            examList.removeViewAt(examIndex - 1);
        }

        addNoExamText();
    }

    private boolean pastAllowed() {
        String pastSetting = PreferenceHelper.getPreference(getApplicationContext(), "showPast");
        if (pastSetting.equals("0")) {
            return false;
        } else {
            PreferenceHelper.setPreference(getApplicationContext(), "1", "showPast");
            return true;
        }
    }

    public void showUpdateCard(String version) {
        updateCard = (ConstraintLayout) lectureEndLayout.findViewById(R.id.updateCard);

        TextView updateVersionText = (TextView) updateCard.findViewById(R.id.updateVersionText);
        updateVersionText.setText(getString(R.string.text_version) + " " + version);

        Button updateDownloadButton = (Button) updateCard.findViewById(R.id.updateDownloadButton);
        updateDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://nico-assfalg.de/Clausura/updateChecker.php?thisversion="
                        + PreferenceHelper.getAppVersion(getApplicationContext());
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    builder.setToolbarColor(getColor(R.color.colorPrimary));
                }
                CustomTabsIntent intent = builder.build();
                intent.launchUrl(MainActivity.this, Uri.parse(url));
            }
        });

        updateCard.setVisibility(View.VISIBLE);
    }

    private void checkForUpdate() {
        if (!checkWifiOnAndConnected()) {
            return;
        }
        AsyncTask<String, Integer, String> checkerTask = new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                URL versionUrl = null;
                try {
                    versionUrl = new URL("http://clausura.nico-assfalg.de/version.php?thisversion=" + params[0]);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(versionUrl.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String version;
                try {
                    if ((version = in.readLine()) != null)
                        return version;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "NOPE";
            }

            @Override
            protected void onPostExecute(String updateVersion) {
                if (!updateVersion.contains("NOPE")) {
                    showUpdateCard(updateVersion);
                }
            }
        }.execute(PreferenceHelper.getAppVersion(getApplicationContext()));
    }

    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
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

    private void showPast(boolean enabled) {
        if (!enabled) {
            PreferenceHelper.setPreference(getApplicationContext(), "0", "showPast");
            examList.removeAllViews();
            populate();
            Snackbar snackbar = Snackbar.make(findViewById(coordinatorLayout), "Vergangene Termine werden ausgeblendet.", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            PreferenceHelper.setPreference(getApplicationContext(), "1", "showPast");
            examList.removeAllViews();
            populate();
            Snackbar snackbar = Snackbar.make(findViewById(coordinatorLayout), "Vergangene Termine werden angezeigt.", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
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
