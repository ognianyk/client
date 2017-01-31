package com.ognyanyk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ognyanyk.structure.RConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.mandfer.dht11.DHT11SensorReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by pavelognianyk on 10/25/16.
 */
@Component
public class SendDataController extends BaseController{
    private final Logger logger = LoggerFactory.getLogger(SendDataController.class);
    private DHT11SensorReader sensor = new DHT11SensorReader();



    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() {

        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        this.sensor.setdTHPIN(7);
        float[] readData = sensor.readData();
        stringStringHashMap.put("temperature", readData[0]);
        stringStringHashMap.put("humidity", readData[1]);
        stringStringHashMap.put("heating", true);


        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            restTemplate.exchange(baseHost + "/add_data?temperature={temperature}&humidity={humidity}&heating={heating}", HttpMethod.GET, entity, String.class,
                    stringStringHashMap);
        } catch (Exception e) {
            logger.error("Sending data error. ", e);
        }
    }

//    @Scheduled(fixedRate = 1000)
//    public void updateHeating() throws ConfigurationException {
//
//        boolean heatingStatus = config.getBoolean("heatingStatus", false);
//        boolean enablePeriodicalHeating = config.getBoolean("enablePeriodicalHeating", false);
//        boolean isPeriodicalWasOn = config.getBoolean("isPeriodicalWasOn", false);
//        Long lastChangeTime = config.getLong("lastChangeTime", 0l);
//        Long heatingDurationInSeconds = config.getLong("heatingDurationInSeconds", 0l) * 1000;
//        Long periodInSeconds = config.getLong("periodInSeconds", 0l) * 1000;
//
//        if (heatingStatus) {
//            relayPin.setMode(PinMode.DIGITAL_OUTPUT);
//        } else if (enablePeriodicalHeating && isPeriodicalWasOn && (new Date()).getTime() - (lastChangeTime + heatingDurationInSeconds) > 0) {
//            relayPin.setMode(PinMode.DIGITAL_INPUT);
//            config.setProperty("lastChangeTime", (new Date()).getTime());
//            config.setProperty("isPeriodicalWasOn", false);
//        } else if (enablePeriodicalHeating && !isPeriodicalWasOn && ((new Date()).getTime() - (lastChangeTime + periodInSeconds)) > 0) {
//            relayPin.setMode(PinMode.DIGITAL_OUTPUT);
//            config.setProperty("lastChangeTime", (new Date()).getTime());
//            config.setProperty("isPeriodicalWasOn", true);
//        } else if (!enablePeriodicalHeating && !heatingStatus) {
//            relayPin.setMode(PinMode.DIGITAL_INPUT);
//        }
//        config.save(new File("config.properties"));
//        logger.info("Sync. heating status [{}]", relayPin.isMode(PinMode.DIGITAL_OUTPUT));
//    }


}
