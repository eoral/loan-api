package com.eoral.loanapi.service;

import com.eoral.loanapi.entity.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Customer checkCustomer(Long customerId);

    void increaseUsedCreditLimit(Customer customer, BigDecimal amount);

    void decreaseUsedCreditLimit(Customer customer, BigDecimal amount);

}
