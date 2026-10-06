package com.example.springbootdemo.service;

import com.example.springbootdemo.dto.CustomerRequest;
import com.example.springbootdemo.dto.CustomerResponse;
import com.example.springbootdemo.entity.Customer;
import com.example.springbootdemo.exception.CustomerNotFoundException;
import com.example.springbootdemo.exception.ResourceNotFoundException;
import com.example.springbootdemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    public CustomerService(CustomerRepository customerRepository, AuditService auditService) {
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with email " + request.getEmail() + " already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        String custNum = request.getCustomerNumber();
        if (custNum == null || custNum.isBlank()) {
            custNum = "CUST" + (1000 + (customerRepository.count() + 1));
        }
        customer.setCustomerNumber(custNum);

        Customer savedCustomer = customerRepository.save(customer);

        auditService.logAction("CREATE_CUSTOMER", "CUSTOMER", savedCustomer.getId().toString(),
                "Created customer: " + savedCustomer.getName() + " (" + savedCustomer.getCustomerNumber() + ")");

        return convertToResponse(savedCustomer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        return convertToResponse(customer);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            customer.setStatus(request.getStatus());
        }

        Customer updatedCustomer = customerRepository.save(customer);

        auditService.logAction("UPDATE_CUSTOMER", "CUSTOMER", updatedCustomer.getId().toString(),
                "Updated customer details for " + updatedCustomer.getName());

        return convertToResponse(updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerRepository.delete(customer);

        auditService.logAction("DELETE_CUSTOMER", "CUSTOMER", id.toString(),
                "Deleted customer " + customer.getName());
    }

    public CustomerResponse convertToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getStatus(),
                customer.getCreatedAt()
        );
    }
}
