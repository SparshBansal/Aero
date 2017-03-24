package com.awesomedev.smartindiahackathon.Models.Route;

import java.util.List;

/**
 * Created by sparsh on 3/24/17.
 */

public class Routes {
    String summary;
    List<Legs> legs;

    public String getSummary() {
        return summary;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }
}
