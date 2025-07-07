package com.myproject.microservices.licensingservice.controller;


import com.myproject.microservices.licensingservice.model.License;
import com.myproject.microservices.licensingservice.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/organizations/{organizationId}/licenses")
public class LicenseController {
    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService){
        this.licenseService = licenseService;
    }

    @GetMapping(value = "/{licenseId}")
    public ResponseEntity<License> getLicense(
            @PathVariable("licenseId") String licenseId,
            @PathVariable("organizationId") String organizationId
    ){
        License license = licenseService.getLicense(licenseId,organizationId);
        return ResponseEntity.ok(license);
    }

    @PutMapping
    public ResponseEntity<String> updateLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request
    ){
        return ResponseEntity.ok(licenseService.updateLicense(request,organizationId));
    }

    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request
    ){
        return ResponseEntity.ok(licenseService.createLicense(request,organizationId));
    }

    @DeleteMapping(value = "/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("licenseId") String licenseId,
            @PathVariable("organizationId") String organizationId
    ){
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId,organizationId));
    }

}
