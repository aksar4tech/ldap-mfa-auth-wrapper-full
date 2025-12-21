package com.example.auth.domain;

public enum AuditEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    MFA_TRIGGERED,
    MFA_SUCCESS,
    MFA_FAILURE,
    RATE_LIMIT_EXCEEDED
}
