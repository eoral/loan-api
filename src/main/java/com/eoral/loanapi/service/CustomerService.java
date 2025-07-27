package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.entity.Customer;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomer(Long customerId);

    Customer checkCustomer(Long customerId);

    void checkCustomerCanBeManagedByCurrentUser(Customer customer);

    void increaseUsedCreditLimit(Customer customer, BigDecimal amount);

    void decreaseUsedCreditLimit(Customer customer, BigDecimal amount);

}
