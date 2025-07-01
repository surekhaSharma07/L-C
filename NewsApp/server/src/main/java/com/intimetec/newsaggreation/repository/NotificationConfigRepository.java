//package com.intimetec.newsaggreation.repository;
//
//import com.intimetec.newsaggreation.model.NotificationConfig;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {
//}
package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.NotificationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationConfigRepository extends JpaRepository<NotificationConfig, Long> {
    Optional<NotificationConfig> findByUserId(Long userId);
}
