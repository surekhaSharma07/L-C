package com.intimetec.newsaggreation.service.impl;

import com.intimetec.newsaggreation.dto.ApiSourceDto;
import com.intimetec.newsaggreation.model.ApiSource;
import com.intimetec.newsaggreation.repository.ApiSourceRepository;
import com.intimetec.newsaggreation.service.ApiSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiSourceServiceImpl implements ApiSourceService {

    private final ApiSourceRepository repo;
    private final RestTemplate restTemplate = new RestTemplate();   // simple health‑check

    /* 1️⃣  List with live status */
    @Override
    public List<ApiSourceDto> findAll() {
        return repo.findAll().stream()
                .map(this::toDtoWithStatus)
                .collect(Collectors.toList());
    }

    /* 2️⃣  Single source detail */
    @Override
    public ApiSourceDto findById(Integer id) {
        return repo.findById(id)
                .map(this::toDtoWithStatus)
                .orElseThrow(() -> new IllegalArgumentException("ApiSource not found"));
    }

    /* 3️⃣  Create OR update */
    @Override
    public ApiSourceDto save(ApiSourceDto dto) {
        ApiSource entity = dto.getId() == null
                ? new ApiSource()
                : repo.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("ApiSource not found"));

        BeanUtils.copyProperties(dto, entity, "id", "status");
        ApiSource saved = repo.save(entity);
        return toDtoWithStatus(saved);
    }

    private ApiSourceDto toDtoWithStatus(ApiSource e) {
        ApiSourceDto d = new ApiSourceDto();
        BeanUtils.copyProperties(e, d);
        try {
            restTemplate.getForEntity(e.getEndpointUrl(), String.class);
            d.setStatus("UP");
        } catch (RestClientException ex) {
            d.setStatus("DOWN");
        }
        return d;
    }
}
