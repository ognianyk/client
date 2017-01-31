package com.ognyanyk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by pavelognianyk on 10/24/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RConfig implements Serializable{

    private boolean heatingStatus;

    private Date changeTime;

    private boolean enablePeriodicalHeating;

    private Long periodInSeconds;

    private Long heatingDurationInSeconds;

    public RConfig() {
    }


    public boolean isHeatingStatus() {
        return heatingStatus;
    }

    public RConfig setHeatingStatus(boolean heatingStatus) {
        this.heatingStatus = heatingStatus;
        return this;
    }

    public Date getChangeTime() {
        return changeTime;
    }

    public RConfig setChangeTime(Date changeTime) {
        this.changeTime = changeTime;
        return this;
    }

    public boolean isEnablePeriodicalHeating() {
        return enablePeriodicalHeating;
    }

    public RConfig setEnablePeriodicalHeating(boolean enablePeriodicalHeating) {
        this.enablePeriodicalHeating = enablePeriodicalHeating;
        return this;
    }

    public Long getPeriodInSeconds() {
        return periodInSeconds;
    }

    public RConfig setPeriodInSeconds(Long periodInSeconds) {
        this.periodInSeconds = periodInSeconds;
        return this;
    }

    public Long getHeatingDurationInSeconds() {
        return heatingDurationInSeconds;
    }

    public RConfig setHeatingDurationInSeconds(Long heatingDurationInSeconds) {
        this.heatingDurationInSeconds = heatingDurationInSeconds;
        return this;
    }
}
