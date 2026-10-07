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

                // ==========================================
                // PUBLIC ENDPOINTS
                // ==========================================

                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                .requestMatchers("/health", "/api/info")
                .permitAll()


                // ==========================================
                // CUSTOMERS
                // ==========================================

                // All authenticated users can view customers
                .requestMatchers(HttpMethod.GET, "/api/customers")
                .authenticated()

                .requestMatchers(HttpMethod.GET, "/api/customers/**")
                .authenticated()

                // Only ADMIN can create customers
                .requestMatchers(HttpMethod.POST, "/api/customers")
                .hasRole("ADMIN")

                // Only ADMIN can update customers
                .requestMatchers(HttpMethod.PUT, "/api/customers/**")
                .hasRole("ADMIN")

                // Only ADMIN can delete customers
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**")
                .hasRole("ADMIN")


                // ==========================================
                // ACCOUNTS
                // ==========================================

                // All authenticated users can view accounts
                .requestMatchers(HttpMethod.GET, "/api/accounts")
                .authenticated()

                .requestMatchers(HttpMethod.GET, "/api/accounts/**")
                .authenticated()

                // Only ADMIN can create accounts
                .requestMatchers(HttpMethod.POST, "/api/accounts")
                .hasRole("ADMIN")

                // Only ADMIN can update accounts
                .requestMatchers(HttpMethod.PUT, "/api/accounts/**")
                .hasRole("ADMIN")

                // Only ADMIN can delete accounts
                .requestMatchers(HttpMethod.DELETE, "/api/accounts/**")
                .hasRole("ADMIN")


                // ==========================================
                // TRANSACTIONS
                // ==========================================

                // All authenticated users can view transactions
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/accounts/*/transactions"
                )
                .authenticated()

                // MAKER can create transactions
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/accounts/*/transactions"
                )
                .hasAnyRole("MAKER", "ADMIN")


                // ==========================================
                // BENEFICIARIES
                // ==========================================

                // All authenticated users can view beneficiaries
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/beneficiaries"
                )
                .authenticated()

                // Authenticated banking users can add beneficiaries
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/beneficiaries"
                )
                .hasAnyRole(
                    "USER",
                    "MAKER",
                    "CHECKER",
                    "ADMIN"
                )

                // ADMIN and CHECKER can delete beneficiaries
                .requestMatchers(
                    HttpMethod.DELETE,
                    "/api/beneficiaries/**"
                )
                .hasAnyRole("ADMIN", "CHECKER")


                // ==========================================
                // MONEY TRANSFERS
                // ==========================================

                .requestMatchers(
                    HttpMethod.POST,
                    "/api/transfers"
                )
                .hasAnyRole("MAKER", "USER", "ADMIN")


                // ==========================================
                // AUDIT LOGS
                // ==========================================

                .requestMatchers(
                    "/api/audit-logs/**"
                )
                .hasRole("ADMIN")

                // ==========================================
                // CONSENTS
                // ==========================================

                // All authenticated users can view consents
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/consents"
                )
                .authenticated()

                .requestMatchers(
                    HttpMethod.GET,
                    "/api/consents/**"
                )
                .authenticated()

                // USER and ADMIN can create consent requests
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/consents"
                )
                .hasAnyRole("USER", "ADMIN")

                // CHECKER and ADMIN can approve consent
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/consents/*/approve"
                )
                .hasAnyRole("CHECKER", "ADMIN")

                // CHECKER and ADMIN can reject consent
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/consents/*/reject"
                )
                .hasAnyRole("CHECKER", "ADMIN")



                // ==========================================
                // EVERYTHING ELSE
                // ==========================================

                .anyRequest()
                .authenticated()
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

        Object rolesObject =
                realmAccess.get("roles");

        if (!(rolesObject instanceof Collection<?> roles)) {
            return Collections.emptyList();
        }

        List<GrantedAuthority> authorities =
                roles.stream()
                    .map(Object::toString)
                    .map(role ->
                        (GrantedAuthority)
                        new SimpleGrantedAuthority(
                            "ROLE_" + role
                        )
                    )
                    .toList();

        return authorities;
    }
}