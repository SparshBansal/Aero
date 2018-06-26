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

    public static final String DB_NAME = "database";
    public static final int DB_VERSION = 4;

    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
                CounterEntry.COLUMN_COUNTER_COUNT + "  INTEGER , " +
                CounterEntry.COLUMN_COUNTER_THROUGHPUT + " REAL , " +
                CounterEntry.COLUMN_COUNTER_AVG_WAITING_TIME + " REAL , "+

                "FOREIGN KEY (" + CounterEntry.COLUMN_CARRIER_KEY + " ) " +
                "REFERENCES " + CarrierEntry.TABLE_NAME + "(" + CarrierEntry._ID +")," +

                "UNIQUE (" + CounterEntry.COLUMN_COUNTER_NUMBER + "," + CounterEntry.COLUMN_CARRIER_KEY + ") ON CONFLICT REPLACE);";

        final String CREATE_FLIGHT_TABLE_COMMAND = "CREATE TABLE " + FlightEntry.TABLE_NAME + " ( " +
                FlightEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                FlightEntry.COLUMN_CARRIER_KEY + " INTEGER , "+
                FlightEntry.COLUMN_FLIGHT_NUMBER + " TEXT, " +
                FlightEntry.COLUMN_SOURCE + " TEXT, " +
                FlightEntry.COLUMN_DESTINATION + " TEXT, "+
                FlightEntry.COLUMN_DEPARTURE_TIME + " TEXT," +
                FlightEntry.COLUMN_DELAYED + " TEXT , " +
                "FOREIGN KEY (" + FlightEntry.COLUMN_CARRIER_KEY + " ) REFERENCES "+
                CarrierEntry.TABLE_NAME + " ( " + CarrierEntry._ID + " ), " +
                "UNIQUE ("+ FlightEntry.COLUMN_FLIGHT_NUMBER + "," + FlightEntry.COLUMN_CARRIER_KEY + ") ON CONFLICT REPLACE);";


        // Create the tables
        db.execSQL(CREATE_AIRPORT_TABLE_COMMAND);
        db.execSQL(CREATE_CARRIER_TABLE_COMMAND);
        db.execSQL(CREATE_COUNTER_TABLE_COMMAND);
        db.execSQL(CREATE_FLIGHT_TABLE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String DROP_TABLE_AIRPORT = "DROP TABLE IF EXISTS " + AirportEntry.TABLE_NAME;
        final String DROP_TABLE_CARRIER = "DROP TABLE IF EXISTS " + CarrierEntry.TABLE_NAME;
        final String DROP_TABLE_COUNTER = "DROP TABLE IF EXISTS " + CounterEntry.TABLE_NAME;
        final String DROP_FLIGHT_TABLE = "DROP TABLE IF EXISTS " + FlightEntry.TABLE_NAME;

        db.execSQL(DROP_TABLE_COUNTER);
        db.execSQL(DROP_TABLE_CARRIER);
        db.execSQL(DROP_TABLE_AIRPORT);
        db.execSQL(DROP_FLIGHT_TABLE);

        onCreate(db);
    }
}
