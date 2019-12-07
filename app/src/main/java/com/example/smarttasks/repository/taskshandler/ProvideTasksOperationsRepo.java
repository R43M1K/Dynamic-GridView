package com.example.smarttasks.repository.taskshandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.smarttasks.repository.ProvideTasksOperationsInter;
import com.example.smarttasks.repository.database.DatabaseHelper;
import com.example.smarttasks.repository.database.DatabaseParams;
import com.example.smarttasks.repository.services.preferences.PreferencesService;
import com.example.smarttasks.repository.services.preferences.PreferencesServiceInter;

import java.util.ArrayList;
import java.util.HashMap;

public class ProvideTasksOperationsRepo implements ProvideTasksOperationsInter {

    private final String TAG = getClass().toString();

    //Database
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    //SharedPreferences
    private PreferencesServiceInter preferences;
    private String TABLE_NAME;

    public ProvideTasksOperationsRepo(Context context) {
        helper = new DatabaseHelper(context);
        db = helper.getWritableDatabase();
        preferences = PreferencesService.getInstance(context);
        TABLE_NAME = preferences.get("currentTableName", "");
    }

    @Override
    public void addTasksList(String taskListRealName, ArrayList<String> tasksList) {
        //TODO Before using this method in MainActivity , you should delete delete sharedPreferences from
        // MainActivity via getApplicationContext.deleteSharedPreferences().
        helper.onCreate(db);
        TABLE_NAME = preferences.get("currentTableName", "");
        ContentValues contentValues = new ContentValues();
        if(!TABLE_NAME.isEmpty()) {
            for (int i = 0; i < tasksList.size(); i++) {
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_NAMES, tasksList.get(i));
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_FINISHED, "Active");
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TABLE_REAL_NAME, taskListRealName);
                db.insert(TABLE_NAME, null, contentValues);
            }
        }
        Log.d(TAG, "Task list is added to table");
    }

    @Override
    public void removeTasksList(String taskListTableName) {
        helper.removeTable(db, taskListTableName);
        Log.d(TAG, "Table removed from database");
    }

    @Override
    public void addTasks(String taskListRealName, String taskListTableName, ArrayList<HashMap<String, String>> tasksList) {
        preferences.put("currentTableName", taskListTableName);
        TABLE_NAME = taskListTableName;
        ContentValues contentValues = new ContentValues();
        if(!taskListTableName.isEmpty()) {
            for (int i = 0; i < tasksList.size(); i++) {
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_NAMES, tasksList.get(i).get("taskName"));
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_FINISHED, tasksList.get(i).get("taskFinished"));
                contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TABLE_REAL_NAME, taskListRealName);
            }
        }
        db.insert(taskListTableName, null, contentValues);
        Log.d(TAG, "Tasks added to table");
    }

    @Override
    public void removeTasks(String taskListTableName, ArrayList<Integer> indexList) {
        preferences.put("currentTableName", taskListTableName);
        TABLE_NAME = taskListTableName;
        if(!taskListTableName.isEmpty()) {
            for(int i=0; i<indexList.size(); i++) {
                String where = DatabaseParams.DatabaseConstants._ID + " = ?";
                String rowID = String.valueOf(indexList.get(i));
                String[] what = {rowID};
                db.delete(taskListTableName,where,what);
                Log.d(TAG, "Tasks removed from table");
            }
        }
    }

    @Override
    public void updateTasks(String taskListTableName, int rowId, String taskName, String taskFinished) {
        preferences.put("currentTableName", taskListTableName);
        TABLE_NAME = taskListTableName;
        ContentValues contentValues = new ContentValues();
        if(!taskListTableName.isEmpty()) {
            contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_NAMES, taskName);
            contentValues.put(DatabaseParams.DatabaseConstants.COLUMN_TASKS_FINISHED, taskFinished);
        }
        String where = DatabaseParams.DatabaseConstants._ID + " LIKE ?";
        String rowID = String.valueOf(rowId);
        String[] what = {rowID};
        db.update(taskListTableName, contentValues, where, what);
        Log.d(TAG, "Tasks updated");
    }

    @Override
    public ArrayList<HashMap> getAllTasks(String taskListTableName) {
        ArrayList<HashMap> taskList = new ArrayList<>();
        Cursor cursor = db.query(taskListTableName, null, null, null, null, null,
                DatabaseParams.DatabaseConstants.COLUMN_TIMESTAMP + " DESC");
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String taskId = String.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseParams.DatabaseConstants._ID)));
                    String taskName = cursor.getString(cursor.getColumnIndex(DatabaseParams.DatabaseConstants.COLUMN_TASKS_NAMES));
                    String taskFinished = cursor.getString(cursor.getColumnIndex(DatabaseParams.DatabaseConstants.COLUMN_TASKS_FINISHED));
                    String taskListRealName = cursor.getString(cursor.getColumnIndex(DatabaseParams.DatabaseConstants.COLUMN_TABLE_REAL_NAME));
                    HashMap<String, String> taskParams = new HashMap<>();
                    taskParams.put("taskId", taskId);
                    taskParams.put("taskName", taskName);
                    taskParams.put("taskFinished", taskFinished);
                    taskParams.put("taskListRealName", taskListRealName);
                    taskList.add(taskParams);
                } while (cursor.moveToNext());
                Log.d(TAG, "Got all list items from table");
            }
            cursor.close();
        }
        return taskList;
    }

    @Override
    public ArrayList<String> getAllTableNames() {
        ArrayList<String> tableNames = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String tableName = cursor.getString(cursor.getColumnIndex("name"));
                    if(tableName.contains("myTasksTable") ) {
                        tableNames.add(tableName);
                    }
                    cursor.moveToNext();
                }
            }
            Log.d(TAG, "Got all table names");
        }
        cursor.close();
        return tableNames;
    }

}