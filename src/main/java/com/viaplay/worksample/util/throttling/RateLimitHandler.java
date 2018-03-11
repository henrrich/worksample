package com.viaplay.worksample.util.throttling;

import com.google.common.util.concurrent.RateLimiter;
import com.viaplay.worksample.exception.RateLimitingException;
import com.viaplay.worksample.service.impl.ArtistServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RateLimitHandler {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitHandler.class);

    private static final int RATE_LIMIT_PER_SEC = 1;
    private static final int TIMEOUT_RATE_LIMIT_CHECK_MILLISEC = 10;

    private RateLimiter rateLimiter;

    public RateLimitHandler() {
        this.rateLimiter = RateLimiter.create(RATE_LIMIT_PER_SEC);
    }

    public RateLimiter getRateLimiter() {
        return rateLimiter;
    }

    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    public int getRateLimitPerSec() {
        return RATE_LIMIT_PER_SEC;
    }

    public void checkPermit() {
        if (!rateLimiter.tryAcquire(TIMEOUT_RATE_LIMIT_CHECK_MILLISEC, TimeUnit.MILLISECONDS)) {
            logger.warn("Rate limit reached!");
            throw new RateLimitingException("Reached rate limit " + getRateLimitPerSec() + " request per second!");
        }
    }
}
