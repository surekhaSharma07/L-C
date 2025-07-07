// src/main/java/com/intimetec/newsaggreation/service/impl/EmailServiceImpl.java
package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String body) {
        log.info("Sending email to {} with subject", to);
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Email sent to {}", to);
        } catch (Exception exception) {
            log.error("Failed to send email to {}: {}", to, exception.getMessage(), exception);
            throw exception;
        }
    }
}
