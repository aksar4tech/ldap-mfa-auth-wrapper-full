package com.example.auth.domain;

import java.time.Instant;

public record AuditEvent(
        Instant timestamp,
        String username,
        AuditEventType type,
        String description
) {}
