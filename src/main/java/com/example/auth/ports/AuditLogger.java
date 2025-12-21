package com.example.auth.ports;

import com.example.auth.domain.AuditEvent;

public interface AuditLogger {

    void log(AuditEvent event);
}

