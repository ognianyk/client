package com.ognyanyk.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ognyanyk.structure.AdditionalSensor;
import com.ognyanyk.structure.ArduinoTemperatureSensor;
import com.ognyanyk.structure.TransportJson;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.PinMode;
import org.mandfer.dht11.DHT11SensorReader;
import com.pi4j.io.gpio.RaspiPin;
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
public class SendDataController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(SendDataController.class);
    private ObjectMapper objectMapper = new ObjectMapper();


    @Value("${heating.additional.temp.sensor}")
    protected String additionalSensorAddress;

    private DHT11SensorReader sensor = new DHT11SensorReader();


    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() throws JsonProcessingException {
        this.sensor.setdTHPIN(7);
        float[] readData = sensor.readData();
        TransportJson transportJson = new TransportJson();
        
        GpioPin relayPin = provisionGPIO();//provisionDigitalMultipurposePin(RaspiPin.GPIO_00, PinMode.DIGITAL_INPUT);
        if (relayPin == null) {
            System.out.print("relay doesn't exist");
        }
        


        if (additionalSensorAddress != null) {
            String[] address = additionalSensorAddress.split(",");
            logger.debug("Additional sensors []", additionalSensorAddress);
            for (String addres : address) {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> entity = new HttpEntity<>(headers);
                    ResponseEntity<ArduinoTemperatureSensor> responseEntity =
                            restTemplate.exchange(addres, HttpMethod.GET, entity, ArduinoTemperatureSensor.class);
                    AdditionalSensor additionalSensor = new AdditionalSensor().setHumidity(String.valueOf(responseEntity.getBody().getHumidity()))
                            .setTemperature(String.valueOf(responseEntity.getBody().getTemperatureCel()));
                    transportJson.getSensorEntityList().add(additionalSensor);
                } catch (Exception ex) {
                    logger.error("Can't read sensor []", addres);
                }
            }
        }
        String jsonString = objectMapper.writeValueAsString(transportJson
                .setTemperature(String.valueOf(readData[0]))
                .setHumidity(String.valueOf(readData[1]))
                .setHeatingStatus(relayPin.isMode(PinMode.DIGITAL_OUTPUT)));
//        String jsonString = objectMapper.writeValueAsString(transportJson
//                .setTemperature("34.6")
//                .setHumidity("33")
//                .setHeatingStatus(true));

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonString, headers);
            restTemplate.exchange(baseHost + "/add_data", HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            logger.error("Sending data error. ", e);
        }
    }


}
