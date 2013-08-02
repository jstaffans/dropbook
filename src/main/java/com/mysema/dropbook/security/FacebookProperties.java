package com.mysema.dropbook.security;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class FacebookProperties  {

    final Properties props;

    public FacebookProperties() {
        props = new Properties();
        try {
            props.load(new FileInputStream("facebook.properties"));
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    public Properties getProps() {
        return props;
    }
}
