package com.awesomedev.smartindiahackathon.Models.Route;

import java.util.List;

/**
 * Created by sparsh on 3/24/17.
 */

public class Routes {
    String summary;
    List<Legs> legs;
    OverviewPolyline overview_polyline;

    public OverviewPolyline getOverviewPolyline() {
        return overview_polyline;
    }

    public String getSummary() {
        return summary;
    }

    public List<Legs> getLegs() {
        return legs;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overview_polyline = overviewPolyline;
    }

    public void setLegs(List<Legs> legs) {
        this.legs = legs;
    }
}
