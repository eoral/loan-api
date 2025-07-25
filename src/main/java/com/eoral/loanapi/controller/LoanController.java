package com.eoral.loanapi.controller;

import com.eoral.loanapi.dto.*;
import com.eoral.loanapi.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("")
    public LoanResponse createLoan(@RequestBody CreateLoanRequest createLoanRequest, Principal principal) {
        return loanService.createLoan(createLoanRequest, principal.getName());
    }

    @GetMapping("{loanId}/installments")
    public List<LoanInstallmentResponse> getInstallmentsOfLoan(@PathVariable Long loanId) {
        return loanService.getInstallmentsOfLoan(loanId);
    }

    @PostMapping("{loanId}/payment")
    public PayLoanResponse payLoan(@PathVariable Long loanId, @RequestBody PayLoanRequest payLoanRequest) {
        return loanService.payLoan(loanId, payLoanRequest);
    }
}
