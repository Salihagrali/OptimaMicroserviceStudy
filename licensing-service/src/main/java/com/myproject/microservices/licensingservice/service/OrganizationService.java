package com.myproject.microservices.licensingservice.service;

import com.myproject.microservices.licensingservice.model.Organization;
import com.myproject.microservices.licensingservice.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationRestTemplateClient organizationRestTemplateClient;

    public OrganizationService(OrganizationRestTemplateClient organizationRestTemplateClient) {
        this.organizationRestTemplateClient = organizationRestTemplateClient;
    }

    @CircuitBreaker(name = "organizationService")
    public Organization getOrganization(String organizationId){
        return organizationRestTemplateClient.getOrganization(organizationId);
    }
}
