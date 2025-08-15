package com.myproject.microservices.licensingservice.service.client;

import com.myproject.microservices.licensingservice.model.Organization;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


//When both OpenFeign and Eureka exist in the same Spring application, Spring autoconfigure these two with each other.
//For example, we don't need to specify "url" for @FeignClient in this case because we are using Eureka as a service discovery.
//But If we were to make a third party request, we would need to specify the "url" attribute in the @FeignClient annotation.

@FeignClient("organization-service")
public interface OrganizationFeignClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/v1/organization/{organizationId}",
            consumes = "application/json"
    )
    Organization getOrganization(@PathVariable String organizationId);
}
