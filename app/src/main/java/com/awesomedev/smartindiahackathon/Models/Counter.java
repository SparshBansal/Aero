package com.awesomedev.smartindiahackathon.Models;

/**
 * Created by sparsh on 3/31/17.
 */

public class Counter {
    String counterCount,counterNumber,throughput;

    public String getCounterCount() {
        return counterCount;
    }

    public String getCounterNumber() {
        return counterNumber;
    }

    public String getThroughput() {
        return throughput;
    }

    public void setCounterCount(String counterCount) {
        this.counterCount = counterCount;
    }

    public void setCounterNumber(String counterNumber) {
        this.counterNumber = counterNumber;
    }

    public void setThroughput(String throughput) {
        this.throughput = throughput;
    }
}
