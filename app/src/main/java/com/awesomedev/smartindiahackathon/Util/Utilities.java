package com.awesomedev.smartindiahackathon.Util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.awesomedev.smartindiahackathon.Data.DatabaseContract;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
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

    public static String getFriendlyTimeString ( float minutes ){

        int mins = (int) minutes;

        int hours = mins/60;

        String friendlyString = "";

        if ( hours != 0 )
            friendlyString = friendlyString.concat(String.format("%d Hours , ", hours));

        int remMins = mins%60;

        if ( remMins != 0)
            friendlyString = friendlyString.concat(String.format("%02d Minutes", remMins));

        return friendlyString;
    }

    public static List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

}
