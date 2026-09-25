package com.example.springbootdemo.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // GET accounts - any authenticated user
                .requestMatchers(HttpMethod.GET, "/api/accounts")
                .authenticated()

                .requestMatchers(HttpMethod.GET, "/api/accounts/**")
                .authenticated()

                // Create account - ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/accounts")
                .hasRole("ADMIN")

                // Create transaction - MAKER only
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/accounts/*/transactions"
                )
                .hasRole("MAKER")

                // Delete beneficiary - ADMIN or CHECKER
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/beneficiaries/**"
                )
                .hasAnyRole("ADMIN", "CHECKER")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(
                        jwtAuthenticationConverter()
                    )
                )
            );

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
            jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
            this::extractRoles
        );

        return converter;
    }

    private Collection<GrantedAuthority> extractRoles(Jwt jwt) {

    Map<String, Object> realmAccess =
            jwt.getClaim("realm_access");

    if (realmAccess == null) {
        return Collections.emptyList();
    }

    Object rolesObject = realmAccess.get("roles");

    if (!(rolesObject instanceof Collection<?> roles)) {
        return Collections.emptyList();
    }

    List<GrantedAuthority> authorities = roles.stream()
            .map(Object::toString)
            .map(role -> (GrantedAuthority)
                    new SimpleGrantedAuthority("ROLE_" + role))
            .toList();

    return authorities;
}
}