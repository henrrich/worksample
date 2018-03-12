package com.viaplay.worksample.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ratelimit")
public class RateLimitConfig {

    @Value("${ratelimit.rps}")
    private int ratelimitPerSec;

    @Value("${ratelimit.checktimeout}")
    private int checkTimeout;

    public int getRatelimitPerSec() {
        return ratelimitPerSec;
    }

    public void setRatelimitPerSec(int ratelimitPerSec) {
        this.ratelimitPerSec = ratelimitPerSec;
    }

    public int getCheckTimeout() {
        return checkTimeout;
    }

    public void setCheckTimeout(int checkTimeout) {
        this.checkTimeout = checkTimeout;
    }
}
