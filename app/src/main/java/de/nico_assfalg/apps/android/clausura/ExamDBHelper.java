package de.nico_assfalg.apps.android.clausura;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExamDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Exams.db";
    private static final int DATABASE_VERSION = 4;
    public static final String EXAM_TABLE_NAME = "exam";
    public static final String EXAM_COLUMN_ID = "_id";
    public static final String EXAM_COLUMN_TITLE = "title";
    public static final String EXAM_COLUMN_LOCATION = "location";
    public static final String EXAM_COLUMN_DATE = "date";
    public static final String EXAM_COLUMN_TIME = "time";
    public static final String EXAM_COLUMN_NOTES = "notes";

    public ExamDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + EXAM_TABLE_NAME + "(" +
                EXAM_COLUMN_ID + " INTEGER PRIMARY KEY, " + //eindeutige id
                EXAM_COLUMN_TITLE + " TEXT, " + //Fach, Vorlesung etc.
                EXAM_COLUMN_LOCATION + " TEXT, " + //Hörsaal
                EXAM_COLUMN_DATE + " TEXT, " + //Datum im Format yyyy-mm-dd !!!
                EXAM_COLUMN_TIME + " TEXT, " + //Uhrzeit im Format hh-mm
                EXAM_COLUMN_NOTES + " TEXT)" //Freie, mehrzeilige Notizen
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EXAM_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertExam(String title, String date, String time, String location, String notes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAM_COLUMN_TITLE, title);
        contentValues.put(EXAM_COLUMN_LOCATION, location);
        contentValues.put(EXAM_COLUMN_DATE, date);
        contentValues.put(EXAM_COLUMN_TIME, time);
        contentValues.put(EXAM_COLUMN_NOTES, notes);
        db.insert(EXAM_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateExam(Integer id, String title, String date, String time, String location, String notes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAM_COLUMN_TITLE, title);
        contentValues.put(EXAM_COLUMN_LOCATION, location);
        contentValues.put(EXAM_COLUMN_DATE, date);
        contentValues.put(EXAM_COLUMN_TIME, time);
        contentValues.put(EXAM_COLUMN_NOTES, notes);
        db.update(EXAM_TABLE_NAME, contentValues, EXAM_COLUMN_ID + " = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Cursor getExam(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + EXAM_TABLE_NAME + " WHERE " +
            EXAM_COLUMN_ID + "=?", new String[] {Integer.toString(id) } );
        return res;
    }

    public Cursor getAllExams() { //sorted by date!
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + EXAM_TABLE_NAME + " ORDER BY " + EXAM_COLUMN_DATE, null);
        return res;
    }

    public Integer deleteExam(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(EXAM_TABLE_NAME, EXAM_COLUMN_ID + " = ? ", new String[] {Integer.toString(id) } );
    }


}
