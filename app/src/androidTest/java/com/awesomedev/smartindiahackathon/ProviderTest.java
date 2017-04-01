package com.awesomedev.smartindiahackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.awesomedev.smartindiahackathon.Data.DatabaseContract;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;
import static org.junit.Assert.*;

/**
 * Created by sparsh on 4/1/17.
 */

@RunWith(AndroidJUnit4.class)
public class ProviderTest {

    private static Context context = null;
    private static final String TAG = ProviderTest.class.getSimpleName();

    static {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDeleteAllRows() {

        int airportRowsDeleted = context.getContentResolver().delete(AirportEntry.CONTENT_URI, null, null);
        int carrierRowsDeleted = context.getContentResolver().delete(CarrierEntry.CONTENT_URI, null, null);
        int counterRowsDeleted = context.getContentResolver().delete(CounterEntry.CONTENT_URI, null, null);


        assertEquals(true,airportRowsDeleted!=0);
        assertEquals(true,carrierRowsDeleted!=0);
        assertEquals(true,counterRowsDeleted!=0);


        Cursor mCursor = context.getContentResolver().query(AirportEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0,mCursor.getCount());

        mCursor = context.getContentResolver().query(CarrierEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0,mCursor.getCount());

        mCursor = context.getContentResolver().query(CounterEntry.CONTENT_URI,null,null,null,null);
        assertEquals(0,mCursor.getCount());

    }

    @Test
    public void testInsertReadProvider(){
        ContentValues airportValues = getAirportValues();
        context.getContentResolver().insert(AirportEntry.CONTENT_URI,airportValues);
        final String airportSelection = AirportEntry.COLUMN_AIRPORT_NAME + "=?";
        final String airportSelectionArgs[] = new String[]{airportValues.getAsString(AirportEntry.COLUMN_AIRPORT_NAME)};

        Cursor mCursor = context.getContentResolver().query(AirportEntry.CONTENT_URI,
                null,
                AirportEntry.COLUMN_AIRPORT_NAME + " =?" ,
                airportSelectionArgs,
                null
        );

        assertEquals(true,mCursor.getCount() > 0);

        mCursor.moveToFirst();
        long airport_id = mCursor.getInt(mCursor.getColumnIndex(AirportEntry._ID));

        ContentValues carrierValues = getCarrierValues(airport_id);
        context.getContentResolver().insert(CarrierEntry.CONTENT_URI,carrierValues);
        mCursor = context.getContentResolver().query(CarrierEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(true,mCursor.getCount()> 0);

        mCursor = context.getContentResolver().query(CarrierEntry.buildCarrierUri(airport_id),
                null,
                null,
                null,
                null
        );

        assertEquals(true,mCursor.getCount() > 0);
        mCursor.moveToFirst();
        final int carrier_id = mCursor.getInt(mCursor.getColumnIndex(CarrierEntry._ID));

        ContentValues counterValues = getCounterValues(carrier_id);
        context.getContentResolver().insert(CounterEntry.CONTENT_URI,counterValues);
        mCursor = context.getContentResolver().query(CounterEntry.CONTENT_URI ,
                null,
                null,
                null,
                null
        );

        assertEquals(true,mCursor.getCount()>0);

        mCursor = context.getContentResolver().query(CounterEntry.buildCounterUri(carrier_id),
                null,
                null,
                null,
                null
        );

        assertEquals(true,mCursor.getCount() >0);
    }

    @Test
    public void testGetType() {
        context = InstrumentationRegistry.getTargetContext();

        Uri AIRPORT_URI = AirportEntry.CONTENT_URI;
        Uri CARRIER_URI = CarrierEntry.CONTENT_URI;
        Uri COUNTER_URI = CounterEntry.CONTENT_URI;

        Uri CARRIER_WITH_AIRPORT_URI = CarrierEntry.buildCarrierUri(1);
        Uri COUNTER_WITH_CARRIER_URI = CounterEntry.buildCounterUri(1);

        String AIRPORT_TYPE = context.getContentResolver().getType(AIRPORT_URI);
        String CARRIER_TYPE = context.getContentResolver().getType(CARRIER_URI);
        String COUNTER_TYPE = context.getContentResolver().getType(COUNTER_URI);

        String CARRIER_WITH_AIRPORT_TYPE = context.getContentResolver().getType(CARRIER_WITH_AIRPORT_URI);
        String COUNTER_WITH_CARRIER_TYPE = context.getContentResolver().getType(COUNTER_WITH_CARRIER_URI);

        assertEquals(AirportEntry.CONTENT_TYPE, AIRPORT_TYPE);
        assertEquals(CarrierEntry.CONTENT_TYPE, CARRIER_TYPE);
        assertEquals(CounterEntry.CONTENT_TYPE, COUNTER_TYPE);
        assertEquals(CarrierEntry.CONTENT_TYPE, CARRIER_WITH_AIRPORT_TYPE);
        assertEquals(CounterEntry.CONTENT_TYPE, COUNTER_WITH_CARRIER_TYPE);
    }


    private ContentValues getAirportValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(AirportEntry.COLUMN_AIRPORT_NAME , "Indira Gandhi International Airport");
        return contentValues;
    }


    private ContentValues getCarrierValues(long airport_key){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarrierEntry.COLUMN_AIRPORT_KEY,airport_key);
        contentValues.put(CarrierEntry.COLUMN_CARRIER_NAME,"Kingfisher");
        return contentValues;
    }

    private ContentValues getCounterValues(long carrier_id){
        ContentValues values = new ContentValues();
        values.put(CounterEntry.COLUMN_CARRIER_KEY,carrier_id);
        values.put(CounterEntry.COLUMN_COUNTER_COUNT,5);
        values.put(CounterEntry.COLUMN_COUNTER_NUMBER,1);
        values.put(CounterEntry.COLUMN_COUNTER_THROUGHPUT,5.23);

        return values;
    }
}
