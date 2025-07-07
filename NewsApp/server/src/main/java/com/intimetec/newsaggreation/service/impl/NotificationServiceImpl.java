//package com.intimetec.newsaggreation.service.impl;
//
//import com.intimetec.newsaggreation.dto.NotificationConfigDto;
//import com.intimetec.newsaggreation.mapper.NotificationMapper;
//import com.intimetec.newsaggreation.model.NotificationConfig;
//import com.intimetec.newsaggreation.model.User;
//import com.intimetec.newsaggreation.repository.NotificationConfigRepository;
//import com.intimetec.newsaggreation.repository.UserRepository;
//import com.intimetec.newsaggreation.service.NotificationService;
//import jakarta.transaction.Transactional;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationServiceImpl implements NotificationService {
//    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
//    UserRepository userRepository;
//    NotificationConfigRepository configRepo;
//
//    @Override
//    @Transactional
//    public NotificationConfigDto updatePreferences(String email, NotificationConfigDto dto) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        NotificationConfig config = configRepo.findByUserId(user.getId())
//                .orElseGet(() -> createDefaultConfig(user));
//
//        config.setBusiness(dto.isBusiness());
//        config.setEntertainment(dto.isEntertainment());
//        config.setSports(dto.isSports());
//        config.setTechnology(dto.isTechnology());
//
//        // Clear existing keywords and add new ones
//        config.getKeywordTerms().clear();
//
//        for (KeywordDto keywordDto : dto.getKeywords()) {
//            Keyword keyword = keywordRepo.findByTermIgnoreCase(keywordDto.getTerm())
//                    .orElseGet(() -> keywordRepo.save(new Keyword(null, keywordDto.getTerm(), new HashSet<>())));
//            config.getKeywordTerms().add(keyword);
//        }
//
//        NotificationConfig saved = configRepo.save(config);
//        return NotificationMapper.toDto(saved);
//    }
//
//
//}
////////////////////
package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.dto.KeywordDto;
import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.dto.NotificationDto;
import com.intimetec.newsaggreation.mapper.NotificationMapper;
import com.intimetec.newsaggreation.model.*;
import com.intimetec.newsaggreation.repository.*;
import com.intimetec.newsaggreation.service.NotificationService;   // ← your interface
import com.intimetec.newsaggreation.util.NotificationConfigFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ArticleRepository articleRepository;

    @Override
    public List<NotificationDto> findByUserEmail(String email) {
        log.info("Finding notifications for user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new RuntimeException("User not found");
                });

        List<Notification> list = notificationRepository.findByUserOrderBySentAtDesc(user);

        return list.stream()
                .map(notification -> {
                    Article article = articleRepository.findById(notification.getNewsId())
                            .orElse(null);

                    return new NotificationDto(
                            notification.getId(),
                            notification.getType(),
                            article != null ? article.getTitle() : "(article deleted)",
                            article != null ? article.getUrl() : "",
                            notification.getSentAt().toString()
                    );
                })
                .toList();
    }
}

