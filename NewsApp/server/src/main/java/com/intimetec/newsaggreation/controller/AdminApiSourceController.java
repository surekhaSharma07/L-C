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
@PreAuthorize("hasRole('ADMIN')")     // JWT / Spring‑Security gated
public class AdminApiSourceController {

    private final ApiSourceService service;

    /**
     * 1️⃣  List external servers + status
     */
    @GetMapping
    public List<ApiSourceDto> list() {
        return service.findAll();
    }

    /**
     * 2️⃣  View single server
     */
    @GetMapping("/{id}")
    public ApiSourceDto detail(@PathVariable("id") Integer id) {
        return service.findById(id);
    }

    /**
     * 3️⃣  Create new server
     */
    @PostMapping
    public ApiSourceDto create(@Valid @RequestBody ApiSourceDto dto) {
        return service.save(dto);
    }

    /**
     * 3️⃣b Update existing server
     */
    @PutMapping("/{id}")
    public ApiSourceDto update(@PathVariable("id") Integer id,
                               @Valid @RequestBody ApiSourceDto dto) {
        dto.setId(id);
        return service.save(dto);
    }
}
