package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.dto.ApiSourceDto;
import com.intimetec.newsaggreation.model.ApiSource;
import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import com.intimetec.newsaggreation.service.ApiSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiSourceServiceImpl implements ApiSourceService {

    private final ApiSourceRepository apiSourceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<ApiSourceDto> findAll() {
        log.info("Finding all ApiSources");
        return apiSourceRepository.findAll().stream()
                .map(this::toDtoWithStatus)
                .collect(Collectors.toList());
    }

    @Override
    public ApiSourceDto findById(Integer id) {
        log.info("Finding ApiSource by ID: {}", id);
        return apiSourceRepository.findById(id)
                .map(this::toDtoWithStatus)
                .orElseThrow(() -> {
                    log.error("ApiSource with ID {} not found", id);
                    return new IllegalArgumentException("ApiSource not found");
                });
    }

    @Override
    public ApiSourceDto save(ApiSourceDto apiSourceDto) {
        log.info("Saving ApiSource: {}", apiSourceDto);
        ApiSource entity = apiSourceDto.getId() == null
                ? new ApiSource()
                : apiSourceRepository.findById(apiSourceDto.getId())
                .orElseThrow(() -> {
                    log.error("ApiSource with ID {} not found for update", apiSourceDto.getId());
                    return new IllegalArgumentException("ApiSource not found");
                });

        BeanUtils.copyProperties(apiSourceDto, entity, "id", "status");
        ApiSource saved = apiSourceRepository.save(entity);
        log.info("ApiSource saved: {}", saved);
        return toDtoWithStatus(saved);
    }

    private ApiSourceDto toDtoWithStatus(ApiSource apiSource) {
        log.info("Checking status for ApiSource: {}", apiSource.getName());
        ApiSourceDto d = new ApiSourceDto();
        BeanUtils.copyProperties(apiSource, d);
        try {
            restTemplate.getForEntity(apiSource.getEndpointUrl(), String.class);
            d.setStatus("UP");
            log.info("ApiSource {} is UP", apiSource.getName());
        } catch (RestClientException ex) {
            d.setStatus("DOWN");
            log.error("ApiSource {} is DOWN due to: {}", apiSource.getName(), ex.getMessage());
        }
        return d;
    }
}
