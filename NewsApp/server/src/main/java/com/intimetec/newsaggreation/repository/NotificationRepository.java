//package com.intimetec.newsaggreation.repository;
//
//import com.intimetec.newsaggreation.model.Notification;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface NotificationRepository extends JpaRepository<Notification, Long>{
//}
package com.intimetec.newsaggreation.repository;

import com.intimetec.newsaggreation.model.Notification;
import com.intimetec.newsaggreation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByUserAndNewsId(User user, Long newsId);


    List<Notification> findByUserOrderBySentAtDesc(User user);


}
