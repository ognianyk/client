package com.ognyanyk.controller;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;

/**
 * Created by pavelognianyk on 1/31/17.
 */
abstract class BaseController {

    @Value("${heating.server.host}")
    protected String baseHost;
    protected final PropertiesConfiguration config = new PropertiesConfiguration();

    @PostConstruct
    private void init() throws ConfigurationException {
        config.load(new File("config.properties"));
    }
}
