package com.example.auth.ports;

public interface EmailSender {

    void send(String to, String subject, String body);
}
