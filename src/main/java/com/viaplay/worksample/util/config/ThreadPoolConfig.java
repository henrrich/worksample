package com.viaplay.worksample.util.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * A spring bean loads the configuration of thread pool defined in application.properties
 */
@Component
@ConfigurationProperties(prefix = "threadpool")
public class ThreadPoolConfig {

    private int size;
    private int capacity;
    private String prefix;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
