package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.model.Customer;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultCustomerService implements CustomerService {

    private final CustomerRepository customerRepository;

    public DefaultCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}
