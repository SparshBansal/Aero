package com.awesomedev.smartindiahackathon.Interfaces;

import com.awesomedev.smartindiahackathon.Models.FlightDetails;
import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sparsh on 3/23/17.
 */

public interface FlightService {
    @GET("/flight/{airport}/{carrier}")
    Call<FlightDetails> getFlightDetails(@Path("airport") String airport, @Path("carrier") String carrier, @Query("flight") String flight);


}
