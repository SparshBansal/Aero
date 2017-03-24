package com.awesomedev.smartindiahackathon.Util;

import com.awesomedev.smartindiahackathon.Interfaces.FlightService;
import com.awesomedev.smartindiahackathon.Interfaces.GoogleMapsService;
import com.awesomedev.smartindiahackathon.R;

import butterknife.BindString;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sparsh on 3/23/17.
 */

public class RetrofitHelper {

    private static final String FLIGHT_SERVER_BASE_URL = "http://localhost:3000";
    private static final String GOOGLE_MAPS_SERVER_BASE_URL = "https://maps.googleapis.com";

    private static Retrofit retrofitFlightServiceInstance = null;
    private static Retrofit retrofitGoogleMapsInstance = null;

    private static FlightService flightServiceInstance = null;
    private static GoogleMapsService googleMapsServiceInstance = null;

    static {

        retrofitFlightServiceInstance = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(FLIGHT_SERVER_BASE_URL)
                .build();
        flightServiceInstance = retrofitFlightServiceInstance.create(FlightService.class);

        retrofitGoogleMapsInstance = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(GOOGLE_MAPS_SERVER_BASE_URL)
                .build();
        googleMapsServiceInstance = retrofitGoogleMapsInstance.create(GoogleMapsService.class);
    }

    public static FlightService getFlightServiceInstance(){
        return flightServiceInstance;
    }

    public static GoogleMapsService getGoogleMapsServiceInstance(){
        return googleMapsServiceInstance;
    }
}
