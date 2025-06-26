package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
}
