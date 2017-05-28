package de.nico_assfalg.apps.android.clausura;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class BackupRestoreActivity extends AppCompatActivity {

    private final int ID_PERMISSION_STORAGE_BACKUP = 1;
    private final int ID_PERMISSION_STORAGE_RESTORE = 2;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeButtons();
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

        File dbTarget = new File(dbTargetFolder.getPath() + "/backup " + timestamp() + ".db");

        try {
            copy(dbSource, dbTarget);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String timestamp() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar);
        return date.toHumanString() + " " + calendar.get(Calendar.HOUR_OF_DAY)
                + ":" + calendar.get(Calendar.MINUTE)
                + ":" + calendar.get(Calendar.SECOND);
    }

    private void restore() {
        ExamDBHelper dbHelper = new ExamDBHelper(this);

        //TODO: Show FilePicker!
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
                    Snackbar error = Snackbar.make(backupButton, getString(R.string.text_permission_denied_backup), Snackbar.LENGTH_LONG);
                    error.show();
                }
                return;

            case ID_PERMISSION_STORAGE_RESTORE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    restore();
                } else {
                    Snackbar error = Snackbar.make(restoreButton, getString(R.string.text_permission_denied_restore), Snackbar.LENGTH_LONG);
                    error.show();
                }
                //return;
        }
    }
}
