package com.awesomedev.smartindiahackathon.Models;

import java.sql.Time;
import java.util.Date;

/**
 * Created by sparsh on 3/23/17.
 */

public class FlightDetails {
    String flightNo, delayed, departureTime, destination, source;

    public String getFlightNo() {
        return flightNo;
    }

    public String getDelayed() {
        return delayed;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getDestination() {
        return destination;
    }

    public String getSource() {
        return source;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public void setDelayed(String delayed) {
        this.delayed = delayed;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
