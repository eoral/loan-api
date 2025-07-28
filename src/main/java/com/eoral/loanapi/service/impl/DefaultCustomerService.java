package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.ForbiddenException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.service.CustomerService;
import com.eoral.loanapi.service.EntityDtoConversionService;
import com.eoral.loanapi.service.SecurityContextService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultCustomerService implements CustomerService {

    private final EntityDtoConversionService entityDtoConversionService;
    private final SecurityContextService securityContextService;
    private final CustomerRepository customerRepository;

    public DefaultCustomerService(
            EntityDtoConversionService entityDtoConversionService,
            SecurityContextService securityContextService,
            CustomerRepository customerRepository) {
        this.entityDtoConversionService = entityDtoConversionService;
        this.securityContextService = securityContextService;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<Customer> customers;
        if (securityContextService.currentUserHasAdminRole()) {
            // I am returning all records for simplicity. Normally, we should do paging.
            customers = customerRepository.findAll();
        } else {
            // I am returning all records for simplicity. Normally, we should do paging.
            customers = customerRepository.findAllByCreatedBy(securityContextService.getCurrentUsername());
        }
        return customers.stream()
                .map(e -> entityDtoConversionService.convertToCustomerResponse(e))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomer(Long customerId) {
        Customer customer = checkCustomer(customerId);
        checkCustomerCanBeManagedByCurrentUser(customer);
        return entityDtoConversionService.convertToCustomerResponse(customer);
    }

    @Override
    public Customer checkCustomer(Long customerId) {
        if (customerId == null) {
            throw new BadRequestException("Customer is not specified.");
        }
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        return optionalCustomer.orElseThrow(() -> new NotFoundException("Customer is not found."));
    }

    @Override
    public void checkCustomerCanBeManagedByCurrentUser(Customer customer) {
        boolean canBeManagedByUser = (securityContextService.currentUserHasAdminRole()
                || securityContextService.getCurrentUsername().equals(customer.getCreatedBy()));
        if (!canBeManagedByUser) {
            throw new ForbiddenException("Customer cannot be managed by current user.");
        }
    }

    @Override
    public void increaseUsedCreditLimit(Customer customer, BigDecimal amount) {
        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().add(amount);
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);
    }

    @Override
    public void decreaseUsedCreditLimit(Customer customer, BigDecimal amount) {
        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().subtract(amount);
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);
    }
}
