package com.eoral.loanapi.controller;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "List All Customers")
    @GetMapping("")
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @Operation(summary = "Get Customer")
    @GetMapping("{customerId}")
    public CustomerResponse getCustomer(@PathVariable Long customerId) {
        return customerService.getCustomer(customerId);
    }
}
