package com.example.auth.adapters;

import com.example.auth.domain.AuditEvent;
import com.example.auth.ports.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jAuditLoggerAdapter implements AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    @Override
    public void log(AuditEvent event) {
        log.info(
                "time={} user={} type={} desc={}",
                event.timestamp(),
                event.username(),
                event.type(),
                event.description()
        );
    }
}

