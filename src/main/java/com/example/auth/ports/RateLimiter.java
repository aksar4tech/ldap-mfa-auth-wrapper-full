package com.example.auth.ports;

public interface RateLimiter {

    boolean isAllowed(String key);

    void recordFailure(String key);
}

