package com.ognyanyk.structure;

/**
 * Created by pavelognianyk on 2/1/17.
 */
public class Report {
    private String temperature;
    private String humidity;
    private Boolean heating;

    public String getTemperature() {
        return temperature;
    }

    public Report setTemperature(String temperature) {
        this.temperature = temperature;
        return this;
    }

    public String getHumidity() {
        return humidity;
    }

    public Report setHumidity(String humidity) {
        this.humidity = humidity;
        return this;
    }

    public Boolean getHeating() {
        return heating;
    }

    public Report setHeating(Boolean heating) {
        this.heating = heating;
        return this;
    }
}
