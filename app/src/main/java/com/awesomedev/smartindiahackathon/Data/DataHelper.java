package com.awesomedev.smartindiahackathon.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.awesomedev.smartindiahackathon.Models.Counter;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;

/**
 * Created by sparsh on 3/31/17.
 */

public class DataHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "database";
    private static final int DB_VERSION = 1;

    public DataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_AIRPORT_TABLE_COMMAND = "CREATE TABLE " + AirportEntry.TABLE_NAME +
                " (" + AirportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +

                AirportEntry.COLUMN_AIRPORT_NAME + " TEXT," +
                "UNIQUE (" + AirportEntry.COLUMN_AIRPORT_NAME + ") ON CONFLICT IGNORE);";

        final String CREATE_CARRIER_TABLE_COMMAND = "CREATE TABLE " + CarrierEntry.TABLE_NAME +
                " (" + CarrierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                CarrierEntry.COLUMN_AIRPORT_KEY + " INTEGER, " +
                CarrierEntry.COLUMN_CARRIER_NAME + " TEXT, " +
                "FOREIGN KEY (" + CarrierEntry.COLUMN_AIRPORT_KEY + ") " +
                "REFERENCES " + AirportEntry.TABLE_NAME + "("+ AirportEntry._ID +"),"+

                "UNIQUE (" + CarrierEntry.COLUMN_AIRPORT_KEY + "," + CarrierEntry.COLUMN_CARRIER_NAME + ") ON CONFLICT IGNORE);";

        final String CREATE_COUNTER_TABLE_COMMAND = "CREATE TABLE " + CounterEntry.TABLE_NAME +
                " (" + CounterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                CounterEntry.COLUMN_CARRIER_KEY + " INTEGER , " +
                CounterEntry.COLUMN_COUNTER_NUMBER + " INTEGER , " +
                CounterEntry.COLUMN_COUNTER_COUNT + " INTEGER , " +
                CounterEntry.COLUMN_COUNTER_THROUGHPUT + " REAL , " +

                "FOREIGN KEY (" + CounterEntry.COLUMN_CARRIER_KEY + " ) " +
                "REFERENCES " + CarrierEntry.TABLE_NAME + "(" + CarrierEntry._ID +")," +

                "UNIQUE (" + CounterEntry.COLUMN_COUNTER_NUMBER + "," + CounterEntry.COLUMN_CARRIER_KEY + ") ON CONFLICT REPLACE);";


        // Create the tables
        db.execSQL(CREATE_AIRPORT_TABLE_COMMAND);
        db.execSQL(CREATE_CARRIER_TABLE_COMMAND);
        db.execSQL(CREATE_COUNTER_TABLE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
