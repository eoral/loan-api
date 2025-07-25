package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.service.CustomerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Customer checkCustomer(Long customerId) { // todo: consider renaming to getCustomer
        if (customerId == null) {
            throw new BadRequestException("Customer is not specified.");
        }
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        return optionalCustomer.orElseThrow(() -> new NotFoundException("Customer is not found."));
    }

    @Override
    public void increaseUsedCreditLimit(Customer customer, BigDecimal amount) { // todo: consider using customerId
        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().add(amount);
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);
    }

    @Override
    public void decreaseUsedCreditLimit(Customer customer, BigDecimal amount) { // todo: consider using customerId
        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().subtract(amount);
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);
    }
}
