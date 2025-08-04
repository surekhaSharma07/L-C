//package com.intimetec.newsaggreation.service.impl;
//
//import com.intimetec.newsaggreation.service.NotificationConfigService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class NotificationServiceConfigImpl implements NotificationConfigService {
//}
package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.dto.KeywordDto;
import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.mapper.NotificationMapper;
import com.intimetec.newsaggreation.model.Keyword;
import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.KeywordRepository;
import com.intimetec.newsaggreation.repository.NotificationConfigRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.NotificationConfigService;
import com.intimetec.newsaggreation.util.NotificationConfigFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConfigServiceImpl implements NotificationConfigService {

    private final NotificationConfigRepository notificationConfigRepository;
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationConfigDto getCurrentUserConfigByEmail(String email) {
        log.info("Getting notification config for user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for email: {}", email);
                    return new RuntimeException("User not found");
                });

        NotificationConfig config = notificationConfigRepository.findByUserId(user.getId())
                .orElseGet(() -> notificationConfigRepository.save(NotificationConfigFactory.createDefault(user)));

        return NotificationMapper.toDto(config);
    }

    @Override
    public NotificationConfigDto getCurrentUserConfig(Long userId) {
        log.info("Getting notification config for user by id: {}", userId);
        NotificationConfig cfg = notificationConfigRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Config not found for user id: {}", userId);
                    return new IllegalArgumentException("Config not found");
                });
        return NotificationMapper.toDto(cfg);
    }

    @Override
    @Transactional
    public void updateConfig(Long userId, NotificationConfigDto dto) {
        log.info("Updating notification config for user id: {}", userId);
        NotificationConfig notificationConfig = notificationConfigRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Config not found for user id: {}", userId);
                    return new IllegalArgumentException("Config not found");
                });

        notificationConfig.setBusiness(dto.business());
        notificationConfig.setEntertainment(dto.entertainment());
        notificationConfig.setSports(dto.sports());
        notificationConfig.setTechnology(dto.technology());

        notificationConfig.getKeywords().clear();
        for (KeywordDto keywordDto : dto.keywords()) {
            log.info("Processing keyword: {} for user id: {}", keywordDto.term(), userId);
            Keyword keyword = keywordRepository
                    .findByTermIgnoreCase(keywordDto.term())
                    .orElseGet(() -> {
                        Keyword newKeyword = new Keyword();
                        newKeyword.setTerm(keywordDto.term());
                        log.info("Creating new keyword: {} for user id: {}", keywordDto.term(), userId);
                        return keywordRepository.save(newKeyword);
                    });
            notificationConfig.getKeywords().add(keyword);
        }
    }
}