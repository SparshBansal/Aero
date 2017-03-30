package com.awesomedev.smartindiahackathon.Models.Route;

/**
 * Created by sparsh on 3/30/17.
 */

public class Bounds {
    LocationCoordinates northeast;
    LocationCoordinates southwest;

    public LocationCoordinates getNortheast() {
        return northeast;
    }

    public LocationCoordinates getSouthwest() {
        return southwest;
    }

    public void setNortheast(LocationCoordinates northeast) {
        this.northeast = northeast;
    }

    public void setSouthwest(LocationCoordinates southwest) {
        this.southwest = southwest;
    }
}
