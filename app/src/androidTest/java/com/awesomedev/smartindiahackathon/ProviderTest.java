package com.awesomedev.smartindiahackathon;

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


    @Test
    public void testDeleteAllRows() {
        context = InstrumentationRegistry.getContext();

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

}
