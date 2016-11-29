package com.labs.svaithin.goalplanner_v0_01.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.Contacts.SettingsColumns.KEY;

public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.GOAL + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.GOALTITLE + " TEXT NOT NULL," +
                TaskContract.TaskEntry.GOALDONE + " BOOLEAN NOT NULL DEFAULT 0);";

        db.execSQL(createTable);

        createTable = "CREATE TABLE " + TaskContract.TaskEntry.MILESTONE + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.MILESTONETITLE + " TEXT NOT NULL," +
                TaskContract.TaskEntry.MILESTONEDONE + " BOOLEAN NOT NULL DEFAULT 0," +
                TaskContract.TaskEntry.MGOALID + " INTEGER," + " FOREIGN KEY (" +
                TaskContract.TaskEntry.MGOALID+")REFERENCES " +
                TaskContract.TaskEntry.GOAL + "("+ TaskContract.TaskEntry._ID +"));";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.GOAL);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.MILESTONE);
        onCreate(db);
    }
}
