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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationConfigServiceImpl implements NotificationConfigService {

    private final NotificationConfigRepository configRepo;
    private final KeywordRepository keywordRepo;
    private final UserRepository userRepo;


    @Override
    public NotificationConfigDto getCurrentUserConfigByEmail(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        NotificationConfig config = configRepo.findByUserId(user.getId())
                .orElseGet(() -> configRepo.save(NotificationConfigFactory.createDefault(user)));

        return NotificationMapper.toDto(config);
    }

    @Override
    public NotificationConfigDto getCurrentUserConfig(Long userId) {
        NotificationConfig cfg = configRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));
        return NotificationMapper.toDto(cfg);
    }

    @Override
    @Transactional
    public void updateConfig(Long userId, NotificationConfigDto dto) {
        NotificationConfig cfg = configRepo.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Config not found"));

        cfg.setBusiness(dto.business());
        cfg.setEntertainment(dto.entertainment());
        cfg.setSports(dto.sports());
        cfg.setTechnology(dto.technology());

        cfg.getKeywords().clear();
        for (KeywordDto kd : dto.keywords()) {
            Keyword k = keywordRepo
                    .findByTermIgnoreCase(kd.term())
                    .orElseGet(() -> {
                        Keyword nk = new Keyword();
                        nk.setTerm(kd.term());
                        return keywordRepo.save(nk);
                    });
            cfg.getKeywords().add(k);
        }
    }
}