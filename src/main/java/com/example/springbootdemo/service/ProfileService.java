package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.CustomerResponse;
import com.example.springbootdemo.dto.UserProfileResponse;
import com.example.springbootdemo.entity.Customer;
import com.example.springbootdemo.repository.CustomerRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    public ProfileService(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = "Guest";
        String email = "";
        List<String> roles = List.of();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            username = jwt.getClaimAsString("preferred_username");
            if (username == null || username.isBlank()) {
                username = jwt.getSubject();
            }
            email = jwt.getClaimAsString("email");

            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role)
                    .toList();
        } else if (authentication != null) {
            username = authentication.getName();
            roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }

        CustomerResponse customerResponse = null;
        if (email != null && !email.isBlank()) {
            Optional<Customer> customerOpt = customerRepository.findByEmail(email);
            if (customerOpt.isPresent()) {
                customerResponse = customerService.convertToResponse(customerOpt.get());
            }
        }

        if (customerResponse == null) {
            List<Customer> allCustomers = customerRepository.findAll();
            if (!allCustomers.isEmpty()) {
                customerResponse = customerService.convertToResponse(allCustomers.get(0));
            }
        }

        return new UserProfileResponse(username, email, roles, customerResponse);
    }
}
