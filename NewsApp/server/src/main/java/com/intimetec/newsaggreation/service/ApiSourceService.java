package com.intimetec.newsaggreation.service;

import com.intimetec.newsaggreation.dto.ApiSourceDto;

import java.util.List;

public interface ApiSourceService {
    List<ApiSourceDto> findAll();

    ApiSourceDto findById(Integer id);

    ApiSourceDto save(ApiSourceDto dto);
}
