package com.ognyanyk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.impl.GpioPinShutdownImpl;
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
public class SendDataController {
    private final Logger logger = LoggerFactory.getLogger(SendDataController.class);
    //DHT11SensorReader sensor = new DHT11SensorReader();

    @Value("${heating.server.host}")
    private String baseHost;

 //   final GpioController gpio = GpioFactory.getInstance();
  //  GpioPinDigitalMultipurpose relayPin = gpio.provisionDigitalMultipurposePin(RaspiPin.GPIO_00, PinMode.DIGITAL_INPUT);


    private Date hostUnavailable;
    private final PropertiesConfiguration config = new PropertiesConfiguration();

    @PostConstruct
    private void init() throws ConfigurationException {
        config.load(new File("config.properties"));
   //     relayPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF, PinMode.DIGITAL_INPUT);
    }

    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer cf3aa8a8b256da4ea8b1e047b02c23b7");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        //this.sensor.setdTHPIN(7);
        //float[] readData = sensor.readData();
        stringStringHashMap.put("temperature", 17.44);
        stringStringHashMap.put("humidity", 45.77);
        stringStringHashMap.put("heating", true);
        try {
            restTemplate.exchange(baseHost + "/add_data?temperature={temperature}&humidity={humidity}&heating={heating}", HttpMethod.GET, entity, String.class,
                    stringStringHashMap);
        } catch (Exception e) {
            logger.error("Sending data error. ", e);
        }
    }


    @Scheduled(fixedRate = 30000)
    public void readConfigFromServer() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer cf3aa8a8b256da4ea8b1e047b02c23b7");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange = null;
        try {
            exchange = restTemplate.exchange(baseHost + "/get_heating_data", HttpMethod.GET, entity, String.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                RConfig configObject = objectMapper.readValue(exchange.getBody(), RConfig.class);
                config.setProperty("heatingStatus", configObject.isHeatingStatus());
                config.setProperty("changeTime", configObject.getChangeTime().getTime());
                config.setProperty("enablePeriodicalHeating", configObject.isEnablePeriodicalHeating());
                config.setProperty("periodInSeconds", configObject.getPeriodInSeconds());
                config.setProperty("heatingDurationInSeconds", configObject.getHeatingDurationInSeconds());
                config.save(new File("config.properties"));
                hostUnavailable = null;
            } else {
                if (hostUnavailable == null) {
                    hostUnavailable = new Date();
                }
            }

        } catch (Exception e) {
            logger.error("Error while config sync. ", e);
            if (hostUnavailable == null) {
                hostUnavailable = new Date();
            }

        }

    }

    @Scheduled(fixedRate = 3600)
    public void checkHost() throws ConfigurationException {
        if (hostUnavailable != null && ((new Date()).getTime() - hostUnavailable.getTime()) > 7200) {
            config.setProperty("heatingStatus", false);
            config.save(new File("config.properties"));

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
