package com.awesomedev.smartindiahackathon.Data;

import android.provider.BaseColumns;

/**
 * Created by sparsh on 3/31/17.
 */

public class DatabaseContract {

    public static final class AirportEntry implements BaseColumns{

        // Name of the table containing list of all available airports
        public static final String TABLE_NAME = "airport";

        // Column for storing the name of the airport
        public static final String COLUMN_AIRPORT_NAME = "airport_name";
    }

    public static final class CarrierEntry implements BaseColumns{

        // Name of the table containing list of carriers per airport
        public static final String TABLE_NAME = "carrier";

        // Column for storing _id of the airport
        public static final String COLUMN_AIRPORT_KEY = "airport_id";

        // Column for storing the name of the carrier
        public static final String COLUMN_CARRIER_NAME = "carrier_name";
    }

    public static final class CounterEntry implements BaseColumns{

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
    }

}
