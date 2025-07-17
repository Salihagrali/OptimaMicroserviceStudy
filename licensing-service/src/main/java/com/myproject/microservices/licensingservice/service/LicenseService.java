package com.myproject.microservices.licensingservice.service;

import com.myproject.microservices.licensingservice.config.ServiceConfig;
import com.myproject.microservices.licensingservice.model.License;
import com.myproject.microservices.licensingservice.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LicenseService {
    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;

    public LicenseService(@Qualifier("messageSource") MessageSource messages, LicenseRepository licenseRepository, ServiceConfig config) {
        this.messages = messages;
        this.licenseRepository = licenseRepository;
        this.config = config;
    }

    public License getLicense(String licenseId, String organizationId){
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if(license == null){
            throw new IllegalArgumentException(
                    String.format(messages.getMessage(
                            "license.search.error.message",null,null
                    ),licenseId,organizationId)
            );
        }
        return license.withComment(config.getProperty());
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
