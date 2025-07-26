package com.eoral.loanapi.controller;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.service.CustomerService;
import com.eoral.loanapi.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final LoanService loanService;

    public CustomerController(
            CustomerService customerService,
            LoanService loanService) {
        this.customerService = customerService;
        this.loanService = loanService;
    }

    @GetMapping("")
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerResponse getCustomer(@PathVariable Long customerId) {
        return customerService.getCustomer(customerId);
    }

    @GetMapping("{customerId}/loans")
    public List<LoanResponse> getLoansOfCustomer(
            @PathVariable Long customerId,
            @RequestParam(required = false) Integer numberOfInstallments,
            @RequestParam(required = false) Boolean isPaid) {
        return loanService.getLoansOfCustomer(customerId, numberOfInstallments, isPaid);
    }
}
