package com.myproject.microservices.licensingservice.service.client;

import com.myproject.microservices.licensingservice.model.Organization;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class OrganizationDiscoveryClient {

    private final DiscoveryClient discoveryClient;

    public OrganizationDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public Organization getOrganization(String organizationId){
        //This will not have the load-balancer logic.
        // Urls must be full and direct, like http://localhost...
        //Avoids Spring's automatic service discovery and load balancing behavior.
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient.getInstances("organization-service");

        if(instances.isEmpty()){
            return null;
        }
        String serviceUri = String.format("%s/v1/organization/%s",
                instances.get(0).getUri().toString(),
                organizationId);
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null,Organization.class,organizationId
                );
        return restExchange.getBody();
    }
}
