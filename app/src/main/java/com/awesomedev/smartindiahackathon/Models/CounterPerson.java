package com.awesomedev.smartindiahackathon.Models;

/**
 * Created by sparsh on 4/2/17.
 */

public class CounterPerson {
    float avg_waiting_time,throughput;
    int num_persons,counterNumber;

    public CounterPerson() {
    }

    public CounterPerson(float avg_waiting_time, float throughput, int num_persons, int counterNumber) {
        this.avg_waiting_time = avg_waiting_time;
        this.throughput = throughput;
        this.num_persons = num_persons;
        this.counterNumber = counterNumber;
    }

    public int getCounterNumber() {
        return counterNumber;
    }

    public float getAvg_waiting_time() {
        return avg_waiting_time;
    }

    public float getThroughput() {
        return throughput;
    }

    public int getNum_persons() {
        return num_persons;
    }

    public void setCounterNumber(int counterNumber) {
        this.counterNumber = counterNumber;
    }

    public void setAvg_waiting_time(float avg_waiting_time) {
        this.avg_waiting_time = avg_waiting_time;
    }

    public void setThroughput(float throughput) {
        this.throughput = throughput;
    }

    public void setNum_persons(int num_persons) {
        this.num_persons = num_persons;
    }
}
