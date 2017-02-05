package com.ognyanyk.structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pavelognianyk on 2/2/17.
 */
public class TransportJson {
    private String temperature;
    private String humidity;
    private boolean heatingStatus;
    private List<AdditionalSensor> sensorEntityList;

    public String getTemperature() {
        return temperature;
    }

    public TransportJson setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getHumidity() {
        return humidity;
    }

    public TransportJson setHumidity(String humidity) {
        this.humidity = humidity;
        return this;
    }

    public boolean isHeatingStatus() {
        return heatingStatus;
    }

    public TransportJson setHeatingStatus(boolean heatingStatus) {
        this.heatingStatus = heatingStatus;
        return this;
    }

    public List<AdditionalSensor> getSensorEntityList() {
        if(sensorEntityList ==null){
            sensorEntityList = new ArrayList<>();
        }
        return sensorEntityList;
    }

    public TransportJson setSensorEntityList(List<AdditionalSensor> sensorEntityList) {
        this.sensorEntityList = sensorEntityList;
        return this;
    }
}
