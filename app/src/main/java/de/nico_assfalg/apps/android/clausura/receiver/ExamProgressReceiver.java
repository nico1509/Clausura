package de.nico_assfalg.apps.android.clausura.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import de.nico_assfalg.apps.android.clausura.helper.ExamDBHelper;

public class ExamProgressReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("todo_id", -1);
        int progress = intent.getIntExtra("todo_progress", -1);

        if (id == -1) {
            Log.e("ExamProgressReceiver", "Exam Progress Intent is not complete");
            return;
        }

        ExamDBHelper dbHelper = new ExamDBHelper(context);
        Cursor cursor = dbHelper.getExam(id);
        if (!cursor.moveToFirst()) {
            Log.e("ExamProgressReceiver", "Cannot find exam with id " + id);
            return;
        }

        if (!dbHelper.updateExam(
                id,
                cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_TIME)),
                cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_LOCATION)),
                cursor.getString(cursor.getColumnIndex(ExamDBHelper.EXAM_COLUMN_NOTES)),
                progress
        )) {
            Log.e("ExamProgressReceiver", "Cannot update exam with id " + id);
            return;
        }

        Log.v("ExamProgressReceiver", "Successful update of exam with id " + id + " to progress " + progress);

        // Intent runIntent = new Intent(context, MainActivity.class);
    }
}
