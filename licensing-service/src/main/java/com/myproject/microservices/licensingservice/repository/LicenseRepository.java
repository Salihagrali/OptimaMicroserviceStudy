package com.myproject.microservices.licensingservice.repository;

import com.myproject.microservices.licensingservice.model.License;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LicenseRepository extends CrudRepository<License,String> {
    public List<License> findByOrganizationId(String organization);
    public License findByOrganizationIdAndLicenseId(String organization, String licenseId);
}
