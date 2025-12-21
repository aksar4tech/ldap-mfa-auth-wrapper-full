package com.example.auth.adapters;

import com.example.auth.ports.EmailSender;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SmtpEmailSenderAdapter implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSenderAdapter.class);

    private final Session session;
    private final String from;

    public SmtpEmailSenderAdapter(
            String host,
            int port,
            String username,
            String password,
            String from
    ) {
        this.from = from;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        // TODO required for production grade SMTP server to use TSL.
//        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        this.session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    @Override
    public void send(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
        } catch (Exception e) {
            log.info("Email subject: {}, body: {}", subject, body);
            log.error("Error while sending email to: {}, message: {}", to, e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}

