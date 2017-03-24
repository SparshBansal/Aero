package com.awesomedev.smartindiahackathon.Models.Route;

import java.util.List;

/**
 * Created by sparsh on 3/24/17.
 */

public class Legs {
    List<Steps> steps;
    Distance distance;
    Duration duration;
    String end_address;
    String start_address;

    public List<Steps> getSteps() {
        return steps;
    }

    public Distance getDistance() {
        return distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getEnd_address() {
        return end_address;
    }

    public String getStart_address() {
        return start_address;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEnd_address(String end_address) {
        this.end_address = end_address;
    }

    public void setStart_address(String start_address) {
        this.start_address = start_address;
    }
}
