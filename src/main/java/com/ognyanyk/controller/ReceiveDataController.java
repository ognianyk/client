package com.ognyanyk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ognyanyk.structure.RConfig;
import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Date;

/**
 * Created by pavelognianyk on 1/31/17.
 */
@Component
public class ReceiveDataController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(ReceiveDataController.class);
    private Date hostUnavailable;
    @Scheduled(fixedRate = 30000)
    public void readConfigFromServer() {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> exchange;
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

}
