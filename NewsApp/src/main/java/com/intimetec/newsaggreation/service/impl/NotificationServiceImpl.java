package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.service.NotificationService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
    public void sendDailyDigest() { log.info("Sending daily digest"); }
}
