package com.example.auth.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppExecutors {

    public static final ExecutorService AUTH_REQUEST_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    public static final ExecutorService NOTIFICATION_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    public static final ExecutorService VERIFY_REQUEST_EXECUTOR =
            Executors.newVirtualThreadPerTaskExecutor();

    private AppExecutors() {}
}

