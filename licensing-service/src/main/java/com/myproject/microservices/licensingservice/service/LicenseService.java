package com.myproject.microservices.licensingservice.service;

import com.myproject.microservices.licensingservice.config.ServiceConfig;
import com.myproject.microservices.licensingservice.model.License;
import com.myproject.microservices.licensingservice.model.Organization;
import com.myproject.microservices.licensingservice.repository.LicenseRepository;
import com.myproject.microservices.licensingservice.service.client.OrganizationDiscoveryClient;
import com.myproject.microservices.licensingservice.service.client.OrganizationFeignClient;
import com.myproject.microservices.licensingservice.service.client.OrganizationRestTemplateClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

@Service
public class LicenseService {
    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;
    private final OrganizationFeignClient organizationFeignClient;
    private final OrganizationRestTemplateClient organizationRestClient;
    private final OrganizationDiscoveryClient organizationDiscoveryClient;

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    public LicenseService(@Qualifier("messageSource") MessageSource messages, LicenseRepository licenseRepository, ServiceConfig config, OrganizationFeignClient organizationFeignClient, OrganizationRestTemplateClient organizationRestClient, OrganizationDiscoveryClient organizationDiscoveryClient) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
        this.organizationFeignClient = organizationFeignClient;
        this.organizationRestClient = organizationRestClient;
        this.organizationDiscoveryClient = organizationDiscoveryClient;
    }

    public License getLicense(String licenseId, String organizationId,String clientType){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if(license == null){
            throw new IllegalArgumentException(
                    String.format(messages.getMessage(
                            "license.search.error.message",null,null
                    ),licenseId,organizationId)
            );
        }
        Organization organization = retrieveOrganizationInfo(organizationId, clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType){
        Organization organization = null;

        switch (clientType) {
            case "Feign" :
                System.out.println("I am using a feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

    @CircuitBreaker(name = "licenseService",
    fallbackMethod = "buildFallBackLicenseList")
    public List<License> getLicenseByOrganization(String organizationId) throws TimeoutException {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    public List<License> buildFallBackLicenseList(String organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("00000000-0000-0000");
        license.setOrganizationId(organizationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum == 3) sleep();
    }

    private void sleep() throws TimeoutException{
        try{
            Thread.sleep(5000);
            throw new TimeoutException();
        }catch (InterruptedException e){
            logger.error(e.getMessage());
        }
    }

    public License createLicense(License license){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public License updateLicense(License license){
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId){
        String responseMsg = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMsg = String.format(messages.getMessage(
                "license.delete.message",null,null
        ),licenseId);
        return responseMsg;
    }
}
