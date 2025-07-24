package com.eoral.loanapi.controller;

import com.eoral.loanapi.dto.GetLoansOfCustomerRequest;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("")
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{id}/loans")
    public String getLoansOfCustomer(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer numberOfInstallments,
            @RequestParam(required = false) Boolean isPaid) {
        new GetLoansOfCustomerRequest(customerId, numberOfInstallments, isPaid);
        return null;
    }
}
