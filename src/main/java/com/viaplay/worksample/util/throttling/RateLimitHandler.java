package com.viaplay.worksample.util.throttling;

import com.google.common.util.concurrent.RateLimiter;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.util.config.RateLimitConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RateLimitHandler {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitHandler.class);

    @Autowired
    private RateLimitConfig rateLimitConfig;

    private RateLimiter rateLimiter;

    public RateLimitHandler(RateLimitConfig rateLimitConfig) {
        this.rateLimiter = RateLimiter.create(rateLimitConfig.getRatelimitPerSec());
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public int getRateLimitPerSec() {
        return rateLimitConfig.getRatelimitPerSec();
    }

    public void checkPermit() {
        if (!rateLimiter.tryAcquire(rateLimitConfig.getCheckTimeout(), TimeUnit.MILLISECONDS)) {
            logger.warn("Rate limit reached!");
            throw new RateLimitingException("Reached rate limit " + getRateLimitPerSec() + " request per second!");
        }
    }
}
