package com.example.cli;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public final class SpringContextHolder {

    private static final AnnotationConfigApplicationContext CONTEXT =
            new AnnotationConfigApplicationContext("com.example.auth");

    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    private SpringContextHolder() {}
}
