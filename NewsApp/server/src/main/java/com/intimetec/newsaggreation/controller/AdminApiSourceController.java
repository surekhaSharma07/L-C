package com.intimetec.newsaggreation.controller;

import com.intimetec.newsaggreation.dto.ApiSourceDto;
import com.intimetec.newsaggreation.service.ApiSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/apisources")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminApiSourceController {

    private final ApiSourceService service;

    @GetMapping
    public List<ApiSourceDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ApiSourceDto detail(@PathVariable("id") Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ApiSourceDto create(@Valid @RequestBody ApiSourceDto dto) {
        return service.save(dto);
    }

    @PutMapping("/{id}")
    public ApiSourceDto update(@PathVariable("id") Integer id,
                               @Valid @RequestBody ApiSourceDto dto) {
        dto.setId(id);
        return service.save(dto);
    }
}
