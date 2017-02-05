package com.ognyanyk.structure;

/**
 * Created by pavelognianyk on 2/2/17.
 */
public class AdditionalSensor {
    private String temperature;
    private String humidity;

    public String getTemperature() {
        return temperature;
    }

    public AdditionalSensor setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getHumidity() {
        return humidity;
    }

    public AdditionalSensor setHumidity(String humidity) {
        this.humidity = humidity;
        return this;
    }
}
