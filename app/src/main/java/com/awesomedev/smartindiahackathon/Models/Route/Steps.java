package com.awesomedev.smartindiahackathon.Models.Route;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by sparsh on 3/24/17.
 */

public class Steps {
    String travel_mode;
    LocationCoordinates start_location;
    LocationCoordinates end_location;
    Polyline polyline;
    Duration duration;
    String html_instructions;
    Distance distance;

    public String getTravel_mode() {
        return travel_mode;
    }

    public LocationCoordinates getStart_location() {
        return start_location;
    }

    public LocationCoordinates getEnd_location() {
        return end_location;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getHtml_instructions() {
        return html_instructions;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setTravel_mode(String travel_mode) {
        this.travel_mode = travel_mode;
    }

    public void setStart_location(LocationCoordinates start_location) {
        this.start_location = start_location;
    }

    public void setEnd_location(LocationCoordinates end_location) {
        this.end_location = end_location;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setHtml_instructions(String html_instructions) {
        this.html_instructions = html_instructions;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }
}
