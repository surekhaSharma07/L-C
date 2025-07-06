package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.KeywordDto;
import com.intimetec.newsaggreation.dto.NotificationConfigDto;
import com.intimetec.newsaggreation.mapper.NotificationMapper;
import com.intimetec.newsaggreation.model.Keyword;
import com.intimetec.newsaggreation.model.NotificationConfig;
import com.intimetec.newsaggreation.model.User;
import com.intimetec.newsaggreation.repository.KeywordRepository;
import com.intimetec.newsaggreation.repository.NotificationConfigRepository;
import com.intimetec.newsaggreation.repository.UserRepository;
import com.intimetec.newsaggreation.service.impl.NotificationConfigServiceImpl;
import com.intimetec.newsaggreation.util.NotificationConfigFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConfigServiceImplTest {
    @Mock NotificationConfigRepository configRepo;
    @Mock KeywordRepository keywordRepo;
    @Mock UserRepository userRepo;
    @InjectMocks NotificationConfigServiceImpl service;

    User user;
    NotificationConfig config;
    NotificationConfigDto configDto;
    KeywordDto keywordDto;
    Keyword keyword;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        keywordDto = new KeywordDto(10L, "java");
        keyword = new Keyword();
        keyword.setId(10L);
        keyword.setTerm("java");
        config = NotificationConfigFactory.createDefault(user);
        config.setId(100L);
        config.setBusiness(true);
        config.setEntertainment(false);
        config.setSports(true);
        config.setTechnology(false);
        config.setKeywords(new HashSet<>(List.of(keyword)));
        configDto = new NotificationConfigDto(
                true, false, true, false, List.of(keywordDto)
        );
    }

    @Test
    void getCurrentUserConfigByEmail_success_existingConfig() {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(configRepo.findByUserId(1L)).thenReturn(Optional.of(config));
        NotificationConfigDto result = service.getCurrentUserConfigByEmail("user@example.com");
        assertNotNull(result);
        assertTrue(result.business());
        assertFalse(result.entertainment());
        verify(userRepo).findByEmail("user@example.com");
        verify(configRepo).findByUserId(1L);
    }

    @Test
    void getCurrentUserConfigByEmail_createsDefaultIfMissing() {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(configRepo.findByUserId(1L)).thenReturn(Optional.empty());
        when(configRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        NotificationConfigDto result = service.getCurrentUserConfigByEmail("user@example.com");
        assertNotNull(result);
        verify(configRepo).save(any(NotificationConfig.class));
    }

    @Test
    void getCurrentUserConfigByEmail_userNotFound() {
        when(userRepo.findByEmail("user@example.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            service.getCurrentUserConfigByEmail("user@example.com")
        );
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void getCurrentUserConfig_success() {
        when(configRepo.findByUserId(1L)).thenReturn(Optional.of(config));
        NotificationConfigDto result = service.getCurrentUserConfig(1L);
        assertNotNull(result);
        assertTrue(result.business());
        verify(configRepo).findByUserId(1L);
    }

    @Test
    void getCurrentUserConfig_configNotFound() {
        when(configRepo.findByUserId(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            service.getCurrentUserConfig(1L)
        );
        assertEquals("Config not found", ex.getMessage());
    }

    @Test
    void updateConfig_success_existingKeyword() {
        NotificationConfig configCopy = NotificationConfigFactory.createDefault(user);
        configCopy.setId(100L);
        when(configRepo.findByUserId(1L)).thenReturn(Optional.of(configCopy));
        when(keywordRepo.findByTermIgnoreCase("java")).thenReturn(Optional.of(keyword));
        service.updateConfig(1L, configDto);
        assertTrue(configCopy.isBusiness());
        assertFalse(configCopy.isEntertainment());
        assertTrue(configCopy.getKeywords().contains(keyword));
        verify(keywordRepo).findByTermIgnoreCase("java");
        verify(configRepo).findByUserId(1L);
    }

    @Test
    void updateConfig_success_newKeyword() {
        NotificationConfig configCopy = NotificationConfigFactory.createDefault(user);
        configCopy.setId(100L);
        when(configRepo.findByUserId(1L)).thenReturn(Optional.of(configCopy));
        when(keywordRepo.findByTermIgnoreCase("java")).thenReturn(Optional.empty());
        when(keywordRepo.save(any())).thenReturn(keyword);
        service.updateConfig(1L, configDto);
        verify(keywordRepo).save(any(Keyword.class));
        assertTrue(configCopy.getKeywords().stream().anyMatch(k -> k.getTerm().equals("java")));
    }

    @Test
    void updateConfig_configNotFound() {
        when(configRepo.findByUserId(1L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            service.updateConfig(1L, configDto)
        );
        assertEquals("Config not found", ex.getMessage());
    }
} 