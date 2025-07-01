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
import com.intimetec.newsaggreation.mapper.NotificationMapper;
import com.intimetec.newsaggreation.model.Keyword;
import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.KeywordRepository;
import com.intimetec.newsaggreation.repository.NotificationConfigRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.NotificationService;   // ← your interface
import com.intimetec.newsaggreation.util.NotificationConfigFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);
//
//    private final UserRepository userRepository;
//    private final NotificationConfigRepository configRepo;
//    private final KeywordRepository keywordRepo;
//
//    /* -------- GET prefs by email -------------------------------------- */
//    @Override
//    public NotificationConfigDto getCurrentUserConfigByEmail(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        NotificationConfig cfg = configRepo.findByUserId(user.getId())
//                .orElseGet(() -> createAndSaveDefaultConfig(user));
//
//        return NotificationMapper.toDto(cfg);
//    }

    /* -------- UPDATE prefs -------------------------------------------- */
//    @Override
//    @Transactional
//    public NotificationConfigDto updatePreferences(String email, NotificationConfigDto dto) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        NotificationConfig cfg = configRepo.findByUserId(user.getId())
//                .orElseGet(() -> createAndSaveDefaultConfig(user));
//
//        cfg.setBusiness(dto.business());
//        cfg.setEntertainment(dto.entertainment());
//        cfg.setSports(dto.sports());
//        cfg.setTechnology(dto.technology());
//
//        cfg.getKeywords().clear();
//        for (KeywordDto kd : dto.keywords()) {
//            Keyword k = keywordRepo.findByTermIgnoreCase(kd.term())
//                    .orElseGet(() -> keywordRepo.save(new Keyword(null, kd.term(), new HashSet<>())));
//            cfg.getKeywords().add(k);
//        }
//
//        NotificationConfig saved = configRepo.save(cfg);
//        log.debug("Notification preferences updated for {}", email);
//        return NotificationMapper.toDto(saved);
//    }
//
//    /* -------- helper --------------------------------------------------- */
//    private NotificationConfig createAndSaveDefaultConfig(User user) {
//        return configRepo.save(NotificationConfigFactory.createDefault(user));
//    }
}
