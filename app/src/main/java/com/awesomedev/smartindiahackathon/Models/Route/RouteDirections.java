package com.awesomedev.smartindiahackathon.Models.Route;

import java.util.List;

/**
 * Created by sparsh on 3/24/17.
 */

public class RouteDirections {
    String status;
    List<Routes> routes;


    public String getStatus() {
        return status;
    }

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }
}
