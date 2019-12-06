package com.example.smarttasks.repository.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.smarttasks.repository.services.preferences.PreferencesService;
import com.example.smarttasks.repository.services.preferences.PreferencesServiceInter;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final String TAG = getDatabaseName();
    private static final String DATABASE_NAME = "testSmartTasks6.db";//"smartTasks.db";
    private static final int DATABASE_VERSION = 1;
    private static String TABLE_NAME;

    //SharedPreferences
    private PreferencesServiceInter preferencesServiceInter;
    private String userInputTableNameId;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        preferencesServiceInter = PreferencesService.getInstance(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Get last id of table , add +1 to that id, and write that name as string in preferences
        int totalCountId = preferencesServiceInter.get("lastTaskListId", -1);
        totalCountId++;
        preferencesServiceInter.put("lastTaskListId", totalCountId);
        userInputTableNameId = String.valueOf(totalCountId);
        TABLE_NAME = DatabaseParams.DatabaseConstants.TABLE_NAME_PART_1.concat(userInputTableNameId);
        if(!isTableNameTaken(db)) {
            //Create Table
            final String SQL_CREATE_TABLE = "CREATE TABLE " +
                    TABLE_NAME + " (" +
                    DatabaseParams.DatabaseConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DatabaseParams.DatabaseConstants.COLUMN_TASKS_NAMES + " TEXT NOT NULL, " +
                    DatabaseParams.DatabaseConstants.COLUMN_TASKS_FINISHED + " TEXT NOT NULL, " +
                    DatabaseParams.DatabaseConstants.COLUMN_TABLE_REAL_NAME + " TEXT NOT NULL, " +
                    DatabaseParams.DatabaseConstants.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");";
            //Put table in Database
            db.execSQL(SQL_CREATE_TABLE);
            preferencesServiceInter.put("currentTableName", TABLE_NAME);
            Log.d(TAG, "Successfully created table to database");
        }else{
            throw new IllegalStateException("Table name already taken");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Deletes Table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create Table again
        onCreate(db);
    }

    public void removeTable(SQLiteDatabase db, String taskListTableName) {
        db.execSQL("DROP TABLE IF EXISTS " + taskListTableName);
        Log.d(TAG, "Table removed from database");
    }

    private boolean isTableNameTaken(SQLiteDatabase db) {
        if (TABLE_NAME == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", TABLE_NAME});
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
