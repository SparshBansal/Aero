package com.awesomedev.smartindiahackathon.Util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.awesomedev.smartindiahackathon.Data.DatabaseContract;

import java.util.concurrent.TimeUnit;

import static com.awesomedev.smartindiahackathon.Data.DatabaseContract.*;

/**
 * Created by sparsh on 3/24/17.
 */

public class Utilities {
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getAirportId(Context context, String airportName) {
        final String selection = AirportEntry.COLUMN_AIRPORT_NAME + "=?";
        final String selectionArgs[] = new String[]{airportName};

        Cursor mCursor = context.getContentResolver().query(AirportEntry.CONTENT_URI, null, selection, selectionArgs, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            return mCursor.getInt(mCursor.getColumnIndex(AirportEntry._ID));
        } else {
            return -1;
        }
    }

    public static int getCarrierId(Context context, int airportId, String carrierName) {
        final String selection = CarrierEntry.COLUMN_AIRPORT_KEY + " =? AND " + CarrierEntry.COLUMN_CARRIER_NAME + " =? ";
        final String selectionArgs[] = new String[]{Integer.toString(airportId), carrierName};

        Cursor mCursor = context.getContentResolver().query(CarrierEntry.CONTENT_URI, null, selection, selectionArgs, null);
        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            return mCursor.getInt(mCursor.getColumnIndex(CarrierEntry._ID));
        } else {
            return -1;
        }
    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.SECONDS.toHours(TimeUnit.SECONDS.toDays(seconds));
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.SECONDS.toMinutes(TimeUnit.SECONDS.toHours(seconds));
        long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.SECONDS.toSeconds(TimeUnit.SECONDS.toMinutes(seconds));

        String response = "";
        if (day > 0)
            response = day + "Days " + hours + " Hours " + minute + " Minutes " + second + " Seconds ";
        if (second > 0)
            response = second + " Seconds";
        if (minute > 0)
            response = minute + " minutes "  + response;
        if (hours > 0)
            response = hours + " hours " + response;
        if (day > 0)
            response = day + " days " + response;

        return response;
    }
}
