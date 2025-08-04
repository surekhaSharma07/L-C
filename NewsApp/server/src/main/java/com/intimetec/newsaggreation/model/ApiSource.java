package com.intimetec.newsaggreation.model;

import jakarta.persistence.*;

@Entity
@Table(name = "api_sources")
public class ApiSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name = "endpoint_url")
    private String endpointUrl;
    @Column(name = "api_key")
    private String apiKey;
    @Column(name = "polling_freq")
    private Integer pollingFreq;

    public ApiSource() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer i) {
        this.id = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String u) {
        this.endpointUrl = u;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String k) {
        this.apiKey = k;
    }

    public Integer getPollingFreq() {
        return pollingFreq;
    }

    public void setPollingFreq(Integer f) {
        this.pollingFreq = f;
    }
}
