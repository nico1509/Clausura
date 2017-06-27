package de.nico_assfalg.apps.android.clausura.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.nico_assfalg.apps.android.clausura.time.Date;
import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;
import de.nico_assfalg.apps.android.clausura.helper.PreferenceHelper;
import de.nico_assfalg.apps.android.clausura.R;

public class BackupRestoreActivity extends AppCompatActivity {

    private final int ID_PERMISSION_STORAGE_BACKUP = 1;
    private final int ID_PERMISSION_STORAGE_RESTORE = 2;

    private Intent backToMainIntent;

    private Button backupButton;
    private Button restoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_restore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeButtons();
        backToMainIntent = new Intent(BackupRestoreActivity.this, MainActivity.class);
        setTitle(getString(R.string.title_activity_backup_restore));
    }

    @Override
    public void onBackPressed() {
        startActivity(backToMainIntent);
        finish();
    }

    private void initializeButtons() {
        backupButton = (Button) findViewById(R.id.backupButton);
        backupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission(ID_PERMISSION_STORAGE_BACKUP);
            }
        });

        restoreButton = (Button) findViewById(R.id.restoreButton);
        restoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission(ID_PERMISSION_STORAGE_RESTORE);
            }
        });
    }

    private void backup() {
        ExamDBHelper dbHelper = new ExamDBHelper(this);
        File dbSource = dbHelper.getDatabaseFile(this);
        File dbTargetFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Clausura");
        dbTargetFolder.mkdirs();

        File dbTarget = new File(dbTargetFolder.getPath() + File.separator
                + getString(R.string.text_backup_file_prefix) + " " + timestamp() + ".zip");

        try {
            createBackupZip(dbSource, dbTarget);
            showInfoDialog(R.string.dialog_backup_success, backToMainIntent);
        } catch (IOException e) {
            e.printStackTrace();
            showInfoDialog(R.string.dialog_backup_fail_other, null);
        }
    }

    private void createBackupZip(File dbFile, File backupFile) throws IOException {
        //output zip
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(backupFile));

        //Part 1: The Database
        out.putNextEntry(new ZipEntry("database.db"));
        FileInputStream fis = new FileInputStream(dbFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        fis.close();


        //Part 2: The Settings
        out.putNextEntry(new ZipEntry("settings.txt"));
        String lectureEnd = PreferenceHelper.getPreference(this, "endOfLectureDate");
        buffer = lectureEnd.getBytes();
        out.write(buffer, 0, buffer.length);
        out.closeEntry();
        out.close();
    }

    private void readBackupZip(File backupFile, File dbFile) throws IOException {
        //input zip
        ZipInputStream in = new ZipInputStream(new FileInputStream(backupFile));

        //Part 1: The Database
        in.getNextEntry();
        File temp = File.createTempFile("database", "db");
        FileOutputStream fos = new FileOutputStream(temp);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        copy(temp, dbFile);

        //Part 2: The Settings
        ZipEntry txt = in.getNextEntry();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        buffer = new byte[1024];
        while ((length = in.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        os.flush();
        byte[] result = os.toByteArray();
        String lectureEnd = new String(result);
        PreferenceHelper.setPreference(this, lectureEnd, "endOfLectureDate");
        in.close();
    }

    private String timestamp() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar);

        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        hour = hour.length() == 1 ? "0" + hour : hour;

        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        minute = minute.length() == 1 ? "0" + minute : minute;

        String second = String.valueOf(calendar.get(Calendar.SECOND));
        second = second.length() == 1 ? "0" + second : second;

        return date.toHumanString() + " " + hour + ":" + minute + ":" + second;
    }

    private void restore() {
        ExamDBHelper dbHelper = new ExamDBHelper(this);
        final File database = dbHelper.getDatabaseFile(this);

        showFilePickerDialog(database);
    }

    private void showFilePickerDialog(final File database) {
        //Look for backups
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .getPath() + File.separator + "Clausura";
        File backupFolder = new File(path);
        File[] backups = backupFolder.listFiles();
        if (backups == null) { //no backup -> Error
            showInfoDialog(R.string.dialog_restore_fail_no_backups, null);
            return;
        }

        //if backups are present shw file chooser
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_file_chooser);

        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        LinearLayout fileListLayout = (LinearLayout) dialog.findViewById(R.id.fileListLayout);

        for (final File backup : backups) {
            LayoutInflater inflater = getLayoutInflater();

            ConstraintLayout backupLayout = (ConstraintLayout) inflater
                    .inflate(R.layout.layout_file_backup, null, false);

            TextView backupName = (TextView) backupLayout.findViewById(R.id.fileNameText);
            String backupFileName = getString(R.string.text_backup_from) + backup.getName()
                    .replace(getString(R.string.text_backup_file_prefix), "").replace(".zip", "");
            backupName.setText(backupFileName);
            backupName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    try {
                        readBackupZip(backup, database);
                        showInfoDialog(R.string.dialog_restore_success, backToMainIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        showInfoDialog(R.string.dialog_restore_fail_other, null);
                    }
                }
            });

            fileListLayout.addView(backupLayout);

            //Set Width to match_parent
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            dialog.show();
        }
    }

    private void showInfoDialog(int stringResource, Intent intent) {
        final Dialog dialog = new Dialog(this);
        final Intent intentFinal = intent;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_info);

        TextView infoText = (TextView) dialog.findViewById(R.id.infoText);
        infoText.setText(getString(stringResource));

        Button okButton = (Button) dialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (intentFinal != null) {
                    startActivity(intentFinal);
                    finish();
                }
            }
        });

        //Set Width to match_parent
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    private void copy(File src, File tgt) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(tgt);

        byte[] buf = new byte[1024];
        int length;
        while ((length = in.read(buf)) > 0) {
            out.write(buf, 0, length);
        }
        in.close();
        out.close();
    }

    private boolean checkStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestStoragePermission(int requestCode) {
        if (checkStoragePermission()) {
            switch (requestCode) {
                case ID_PERMISSION_STORAGE_BACKUP:
                    backup();
                    return;
                case ID_PERMISSION_STORAGE_RESTORE:
                    restore();
                    //return;
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case ID_PERMISSION_STORAGE_BACKUP:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    backup();
                } else {
                    showInfoDialog(R.string.dialog_backup_fail_permission, null);
                }
                return;

            case ID_PERMISSION_STORAGE_RESTORE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restore();
                } else {
                    showInfoDialog(R.string.dialog_restore_fail_permission, null);
                }
                //return;
        }
    }
}
