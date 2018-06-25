package com.awesomedev.smartindiahackathon.Data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.awesomedev.smartindiahackathon.Activities.DetailsActivity;

/**
 * Created by sparsh on 3/31/17.
 */

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.awesomedev.smartindiahackathon";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_AIRPORT = "airport";
    public static final String PATH_CARRIER = "carrier";
    public static final String PATH_COUNTER = "counter";
    public static final String PATH_FLIGHT = "flight";

    public static final String TAG= DatabaseContract.class.getSimpleName();

    public static final class AirportEntry implements BaseColumns{

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_AIRPORT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_AIRPORT;


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_AIRPORT).build();

        // Name of the table containing list of all available airports
        public static final String TABLE_NAME = "airport";

        // Column for storing the name of the airport
        public static final String COLUMN_AIRPORT_NAME = "airport_name";
    }

    public static final class CarrierEntry implements BaseColumns{
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_CARRIER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_CARRIER;


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARRIER).build();

        // Name of the table containing list of carriers per airport
        public static final String TABLE_NAME = "carrier";

        // Column for storing _id of the airport
        public static final String COLUMN_AIRPORT_KEY = "airport_id";

        // Column for storing the name of the carrier
        public static final String COLUMN_CARRIER_NAME = "carrier_name";

        public static Uri buildCarrierWithAirportUri(long airport_id){
            Uri returnUri = ContentUris.withAppendedId(CONTENT_URI, airport_id);
            Log.d(TAG, "buildCarrierWithAirportUri: " + returnUri.toString());
            return returnUri;
        }

    }

    public static final class CounterEntry implements BaseColumns{

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_COUNTER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_COUNTER;


        // Content uri for querying the entire table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTER).build();

        // Name of the table containing list of counters
        public static final String TABLE_NAME = "counter";

        // Column for storing the _id of the carrier
        public static final String COLUMN_CARRIER_KEY = "carrier_id";

        // Column for storing counter number
        public static final String COLUMN_COUNTER_NUMBER = "counter_number";

        // Column for storing counter count
        public static final String COLUMN_COUNTER_COUNT = "counter_count";

        // Column for storing counter throughput
        public static final String COLUMN_COUNTER_THROUGHPUT = "counter_throughput";

        public static final String COLUMN_COUNTER_AVG_WAITING_TIME = "avg_waiting_time";

        public static Uri buildCounterUri(long carrier_id){
            return ContentUris.withAppendedId(CONTENT_URI,carrier_id);
        }
    }

    public static final class FlightEntry implements BaseColumns{

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" +
                CONTENT_AUTHORITY + "/" + PATH_FLIGHT;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" +
                CONTENT_AUTHORITY + "/" + PATH_FLIGHT;


        // Content uri for querying the entire table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FLIGHT).build();

        // Name of the table containing list of counters
        public static final String TABLE_NAME = "flight";

        // Column for storing the _id of the carrier
        public static final String COLUMN_CARRIER_KEY = "carrier_id";

        // Column for storing counter number
        public static final String COLUMN_FLIGHT_NUMBER = "flight_number";

        // Column for storing counter count
        public static final String COLUMN_DELAYED = "delayed";

        // Column for storing counter throughput
        public static final String COLUMN_DEPARTURE_TIME= "departure_time";

        // Column for storing counter count
        public static final String COLUMN_DESTINATION = "destination";

        // Column for storing counter throughput
        public static final String COLUMN_SOURCE= "source";

        public static Uri getFlightWithCarrierUri ( int carrier_id){
            Uri.Builder builder = CONTENT_URI.buildUpon();
            Uri returnUri = builder.appendPath(PATH_CARRIER).build();
            return ContentUris.withAppendedId(returnUri, carrier_id);
        }

        public static Uri getFlightUri(int flight_id){
            return ContentUris.withAppendedId(CONTENT_URI, flight_id);
        }
    }

}
