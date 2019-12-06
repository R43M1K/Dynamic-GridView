package com.example.smarttasks.repository.database;

import android.provider.BaseColumns;

public final class DatabaseParams {

    private DatabaseParams() {}

    public static final class DatabaseConstants implements BaseColumns{
        public static final String TABLE_NAME_PART_1 = "myTasksTable";
        public static final String COLUMN_TASKS_NAMES = "tasks";
        public static final String COLUMN_TASKS_FINISHED = "finished";
        public static final String COLUMN_TABLE_REAL_NAME = "name";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
