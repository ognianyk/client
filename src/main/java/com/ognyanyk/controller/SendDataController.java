package com.ognyanyk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONWrappedObject;
import com.ognyanyk.structure.RConfig;
import com.ognyanyk.structure.Report;
import com.pi4j.io.gpio.PinMode;
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


/**
 * Created by pavelognianyk on 10/25/16.
 */
@Component
public class SendDataController extends BaseController{
    private final Logger logger = LoggerFactory.getLogger(SendDataController.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private DHT11SensorReader sensor = new DHT11SensorReader();



    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() throws JsonProcessingException {
        this.sensor.setdTHPIN(7);
        float[] readData = sensor.readData();

        String jsonString = objectMapper.writeValueAsString(new Report()
                .setTemperature(String.valueOf(readData[0]))
                .setHumidity(String.valueOf(readData[1]))
                .setHeating(relayPin.isMode(PinMode.DIGITAL_OUTPUT)));

//        String jsonString = objectMapper.writeValueAsString(new Report()
//                .setTemperature("34.6")
//                .setHumidity("33")
//                .setHeating(true));


        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonString,headers);
            restTemplate.exchange(baseHost + "/add_data", HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            logger.error("Sending data error. ", e);
        }
    }


}
