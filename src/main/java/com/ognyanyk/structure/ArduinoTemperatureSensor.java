package com.ognyanyk.structure;

/**
 * Created by pavelognianyk on 2/2/17.
 */
public class ArduinoTemperatureSensor {
    private Float temperatureCel;
    private Float temperatureFah;
    private Float humidity;

    public Float getTemperatureCel() {
        return temperatureCel;
    }

    public ArduinoTemperatureSensor setTemperatureCel(Float temperatureCel) {
        this.temperatureCel = temperatureCel;
        return this;
    }

    public Float getTemperatureFah() {
        return temperatureFah;
    }

    public ArduinoTemperatureSensor setTemperatureFah(Float temperatureFah) {
        this.temperatureFah = temperatureFah;
        return this;
    }

    public Float getHumidity() {
        return humidity;
    }

    public ArduinoTemperatureSensor setHumidity(Float humidity) {
        this.humidity = humidity;
        return this;
    }
}
