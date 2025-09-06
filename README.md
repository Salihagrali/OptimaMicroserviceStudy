I am studying microservices using the book "Spring Microservices in Action, Second Edition" by John Carnell and Illary Huaylupo Sánchez (2021). Since it's been four years since its release, many things in the code have become deprecated. I’m working to resolve these issues while studying and implementing the examples from the book.

I’ll try to keep things simple. Unfortunately, unlike the original GitHub repo provided by the authors, there is no chapter-wise organization. You can check my Git commit history to see what I’ve done and how I’ve resolved the problems I encountered.

After Chapter 9, I decided to study only the concepts because it was very difficult to find all the deprecated codes and fix them. I decided to study the remaining chapters from the book and the github repository.

ABOUT CHAPTER 9

In the book, KeycloakWebSecurityConfigurerAdapter is being used. Because WebSecurityConfigurerAdapter is deprecated, we can't use KeycloakWebSecurityConfigurerAdapter either. We have to use Oauth2ResourceServer to enable Keycloak with our Spring Boot application. Keycloak configuration is almost the same, so I won't touch that.
While authentication with keycloak works, role converter class is not triggered as expected. It could be because of versions. In other projects it works without any error but for some unknown reason i couldn't achieve role-based authorization. I am open to any advice or recommendation.

First we need to add these two dependencies to our pom.xml:
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```



Secondly, we need to configure Keycloak like this:
```
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/realms/demo-realm
```

Then we will have this SecurityConfig class and Role converter class:
```
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtGrantedAuthConverter jwtGrantedAuthConverter;

    public SecurityConfig(JwtGrantedAuthConverter jwtGrantedAuthConverter) {
        this.jwtGrantedAuthConverter = jwtGrantedAuthConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthConverter);

        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/**").hasRole("ostock-admin")
                .requestMatchers("/user/**").hasAnyRole("ostock-user", "ostock-admin")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
            );

        return http.build();
    }
}
```
```
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
```
Commented part is for realm roles.

