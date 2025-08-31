package com.optimagrowth.organizationservice.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtGrantedAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        var realmAccess = jwt.getClaimAsMap("realm-access");

        if(realmAccess == null || realmAccess.isEmpty()){
            return List.of();
        }
        var roles = (List<String>) realmAccess.get("roles");
        log.debug("Realm Access: " + roles.get(0));
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    /*
    private static final String CLAIM_RESOURCE_ACCESS = "resource_access";
    private static final String CLAIM_ROLES = "roles";
    private static final String PREFIX = "ROLE_"; // Spring Security expects this prefix

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Map<String, Object> resourceAccess = jwt.getClaim(CLAIM_RESOURCE_ACCESS);
        if (resourceAccess != null) {
            resourceAccess.forEach((resource, value) -> {
                Map<String, Object> resourceMap = (Map<String, Object>) value;
                Collection<String> roles = (Collection<String>) resourceMap.get(CLAIM_ROLES);
                if (roles != null) {
                    roles.forEach(role -> authorities.add(
                        new SimpleGrantedAuthority(PREFIX + role)
                    ));
                }
            });
        }
        return authorities;
    }
     */
}
