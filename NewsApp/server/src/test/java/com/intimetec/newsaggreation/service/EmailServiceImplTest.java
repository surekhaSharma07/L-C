package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    void testSendEmail_Success() {
        emailService.send("test@example.com", "Test Subject", "Test email body");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithEmptyBody() {
        emailService.send("test@example.com", "Empty Body Test", "");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithSpecialCharacters() {
        emailService.send("test@example.com", "Test with special chars: !@#$%^&*()", "Body with special chars: áéíóú ñ ç");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithLongContent() {
        emailService.send("test@example.com", "Long Subject " + "x".repeat(100), "Long body content " + "y".repeat(1000));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithMultipleRecipients() {
        emailService.send("test1@example.com,test2@example.com", "Multiple Recipients", "This email has multiple recipients");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WhenMailSenderThrowsException() {
        doThrow(new RuntimeException("Mail server error"))
            .when(mailSender).send(any(SimpleMailMessage.class));
        assertThrows(RuntimeException.class, () ->
            emailService.send("test@example.com", "Test Subject", "Test body")
        );
    }

    @Test
    void testSendEmail_WithNullParameters() {
        emailService.send(null, null, null);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmail_WithWhitespaceOnly() {
        emailService.send("   test@example.com   ", "   Test Subject   ", "   Test body   ");
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
} 