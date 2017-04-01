package com.awesomedev.smartindiahackathon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.awesomedev.smartindiahackathon.Data.DataHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.AirportEntry;
import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.CarrierEntry;
import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.CounterEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
/**
 * Created by sparsh on 4/1/17.
 */


@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private static SQLiteOpenHelper mHelper = null;
    private static Context context = null;


    @Test
    public void testCreateDb() throws Exception{
        context = InstrumentationRegistry.getTargetContext();
        context.deleteDatabase(DataHelper.DB_NAME);

        mHelper = new DataHelper(context);

        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();
        assertEquals(true,mDatabase.isOpen());
    }

    @Test
    public void testInsertReadDb() throws Exception{

        SQLiteDatabase mDatabase = mHelper.getWritableDatabase();

        ContentValues airportValues = getAirportValues();
        long airport_id = mDatabase.insert(AirportEntry.TABLE_NAME,null,airportValues);

        Cursor mCursor = mDatabase.query(AirportEntry.TABLE_NAME,null,null,null,null,null,null);
        validateCursor(mCursor,airportValues);

        ContentValues carrierValues = getCarrierValues(airport_id);
        long carrier_id = mDatabase.insert(CarrierEntry.TABLE_NAME,null,carrierValues);

        mCursor = mDatabase.query(CarrierEntry.TABLE_NAME,null,null,null,null,null,null);
        validateCursor(mCursor,carrierValues);

        ContentValues counterValues = getCounterValues(carrier_id);
        long counter_id = mDatabase.insert(CounterEntry.TABLE_NAME , null,counterValues);
        assertNotEquals(counter_id,-1);

        mCursor = mDatabase.query(CounterEntry.TABLE_NAME,null,null,null,null,null,null);
        validateCursor(mCursor,counterValues);
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

    public void validateCursor(Cursor valueCursor , ContentValues expectedValues){

        assertTrue(valueCursor.moveToFirst());
        Set<Map.Entry<String,Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String,Object> entry : valueSet){
            int idx = valueCursor.getColumnIndex(entry.getKey());
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue , valueCursor.getString(idx));
        }
        valueCursor.close();
    }

}
