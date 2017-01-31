package com.ognyanyk.controller;

import com.pi4j.io.gpio.*;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.util.Date;

/**
 * Created by pavelognianyk on 2/1/17.
 */
public class HeatingRelayController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(HeatingRelayController.class);


    final GpioController gpio = GpioFactory.getInstance();
    GpioPinDigitalMultipurpose relayPin = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, PinMode.DIGITAL_INPUT);

    @Scheduled(fixedRate = 1000)
    public void updateHeating() throws ConfigurationException {

        boolean heatingStatus = config.getBoolean("heatingStatus", false);
        boolean enablePeriodicalHeating = config.getBoolean("enablePeriodicalHeating", false);
        boolean isPeriodicalWasOn = config.getBoolean("isPeriodicalWasOn", false);
        Long lastChangeTime = config.getLong("lastChangeTime", 0l);
        Long heatingDurationInSeconds = config.getLong("heatingDurationInSeconds", 0l) * 1000;
        Long periodInSeconds = config.getLong("periodInSeconds", 0l) * 1000;

        if (heatingStatus) {
            relayPin.setMode(PinMode.DIGITAL_OUTPUT);
        } else if (enablePeriodicalHeating && isPeriodicalWasOn && (new Date()).getTime() - (lastChangeTime + heatingDurationInSeconds) > 0) {
            relayPin.setMode(PinMode.DIGITAL_INPUT);
            config.setProperty("lastChangeTime", (new Date()).getTime());
            config.setProperty("isPeriodicalWasOn", false);
        } else if (enablePeriodicalHeating && !isPeriodicalWasOn && ((new Date()).getTime() - (lastChangeTime + periodInSeconds)) > 0) {
            relayPin.setMode(PinMode.DIGITAL_OUTPUT);
            config.setProperty("lastChangeTime", (new Date()).getTime());
            config.setProperty("isPeriodicalWasOn", true);
        } else if (!enablePeriodicalHeating && !heatingStatus) {
            relayPin.setMode(PinMode.DIGITAL_INPUT);
        }
        config.save(new File("config.properties"));
        logger.info("Sync. heating status [{}]", relayPin.isMode(PinMode.DIGITAL_OUTPUT));
    }
}
