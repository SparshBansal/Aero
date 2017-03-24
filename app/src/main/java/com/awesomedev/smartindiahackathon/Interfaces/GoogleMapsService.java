package com.awesomedev.smartindiahackathon.Interfaces;

import com.awesomedev.smartindiahackathon.Models.Route.RouteDirections;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sparsh on 3/24/17.
 */

public interface GoogleMapsService {
    @GET("/maps/api/directions/json")
    Call<RouteDirections> getDirections(
            @Query(encoded = true, value = "origin") String origin,
            @Query(encoded = true, value = "destination") String destination,
            @Query(encoded = true, value = "key") String apiKey
    );
}
