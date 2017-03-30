package com.awesomedev.smartindiahackathon.Models.Route;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sparsh on 3/24/17.
 */

public class LocationCoordinates {
    float lat;
    float lng;

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public LatLng getLatLng(){
        return new LatLng(lat,lng);
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
