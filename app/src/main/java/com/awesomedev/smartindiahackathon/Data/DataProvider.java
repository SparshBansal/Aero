package com.awesomedev.smartindiahackathon.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;

/**
 * Created by sparsh on 4/1/17.
 */

public class DataProvider extends ContentProvider {

    private static final String TAG = DataProvider.class.getSimpleName();
    private SQLiteOpenHelper mHelper = null;
    private SQLiteDatabase mDatabase = null;
    private UriMatcher matcher = buildUriMatcher();


    /*Uri matcher constants*/
    private static final int AIRPORT = 100;
    private static final int CARRIER = 101;
    private static final int COUNTER = 102;
    private static final int CARRIER_WITH_AIRPORT = 103;
    private static final int COUNTER_WITH_CARRIER = 104;

    private static SQLiteQueryBuilder sCarrierByAirportQueryBuilder = null;

    @Override
    public boolean onCreate() {

        mHelper = new DataHelper(getContext());


        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final int match = matcher.match(uri);
        Cursor mCursor = null;
        switch (match){
            case AIRPORT:
                mCursor  = mDatabase.query(AirportEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CARRIER:
                mCursor = mDatabase.query(CarrierEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case COUNTER:
                mCursor = mDatabase.query(CounterEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CARRIER_WITH_AIRPORT:
                long airport_id = ContentUris.parseId(uri);
                final String mSelection = CarrierEntry.COLUMN_AIRPORT_KEY + " = ?";
                final String[] mSelectionArgs = new String[]{Long.toString(airport_id)};

                mCursor = mDatabase.query(CarrierEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case COUNTER_WITH_CARRIER:
                long carrier_id = ContentUris.parseId(uri);

                final String counterSelection = CounterEntry.COLUMN_CARRIER_KEY + " = ? ";
                final String counterSelectionArgs[] = new String[]{ Long.toString(carrier_id) };

                mCursor = mDatabase.query(CounterEntry.TABLE_NAME,
                        projection,
                        counterSelection,
                        counterSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
        }
        mCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return mCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {

            case AIRPORT:
                return AirportEntry.CONTENT_TYPE;
            case CARRIER:
                return CarrierEntry.CONTENT_TYPE;
            case COUNTER:
                return CounterEntry.CONTENT_TYPE;

            case CARRIER_WITH_AIRPORT:
                return CarrierEntry.CONTENT_TYPE;
            case COUNTER_WITH_CARRIER:
                return CounterEntry.CONTENT_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = matcher.match(uri);
        mDatabase = mHelper.getWritableDatabase();
        long _id = -1;
        Uri returnUri = null;
        switch (match){
            case AIRPORT:
                _id = mDatabase.insert(AirportEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    returnUri = uri;
                break;

            case CARRIER:
                _id = mDatabase.insert(CarrierEntry.TABLE_NAME , null , values);
                if (_id > 0)
                    returnUri = uri;
                break;

            case COUNTER:
                _id = mDatabase.insert(CounterEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = uri;
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int match = matcher.match(uri);
        mDatabase = mHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (match){
            case AIRPORT:
                rowsDeleted = mDatabase.delete(AirportEntry.TABLE_NAME,selection,selectionArgs);
                break;

            case CARRIER:
                rowsDeleted = mDatabase.delete(CarrierEntry.TABLE_NAME,selection,selectionArgs);
                break;

            case COUNTER:
                rowsDeleted = mDatabase.delete(CounterEntry.TABLE_NAME,selection,selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = matcher.match(uri);
        mDatabase = mHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (match){
            case AIRPORT:
                rowsUpdated = mDatabase.update(AirportEntry.TABLE_NAME,values,selection,selectionArgs);
                break;

            case CARRIER:
                rowsUpdated = mDatabase.update(CarrierEntry.TABLE_NAME,values,selection,selectionArgs);
                break;

            case COUNTER:
                rowsUpdated = mDatabase.update(CounterEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    private UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(CONTENT_AUTHORITY, PATH_AIRPORT, AIRPORT);
        matcher.addURI(CONTENT_AUTHORITY, PATH_CARRIER, CARRIER);
        matcher.addURI(CONTENT_AUTHORITY, PATH_COUNTER, COUNTER);
        matcher.addURI(CONTENT_AUTHORITY, PATH_CARRIER + "/*", CARRIER_WITH_AIRPORT);
        matcher.addURI(CONTENT_AUTHORITY, PATH_COUNTER + "/*", COUNTER_WITH_CARRIER);

        return matcher;
    }
}