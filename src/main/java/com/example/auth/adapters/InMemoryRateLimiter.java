package com.example.auth.adapters;

import com.example.auth.ports.RateLimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryRateLimiter implements RateLimiter {

    private final Map<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private final int maxAttempts;

    public InMemoryRateLimiter(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    @Override
    public boolean isAllowed(String key) {
        return attempts.getOrDefault(key, new AtomicInteger(0)).get() < maxAttempts;
    }

    @Override
    public void recordFailure(String key) {
        attempts.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }
}
