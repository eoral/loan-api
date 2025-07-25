package com.eoral.loanapi.controller;

import com.eoral.loanapi.dto.*;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.service.LoanService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("")
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }

    @PostMapping("")
    public LoanResponse createLoan(@RequestBody CreateLoanRequest createLoanRequest) {
        return loanService.createLoan(createLoanRequest);
    }

    @GetMapping("test")
    public LoanResponse test() {
        // Long customerId, BigDecimal amount, Integer numberOfInstallments, BigDecimal interestRate
        CreateLoanRequest createLoanRequest = new CreateLoanRequest(1L, BigDecimal.valueOf(120000), 12, BigDecimal.valueOf(0.5));
        return loanService.createLoan(createLoanRequest);
    }

    @GetMapping("{loanId}/installments")
    public List<LoanInstallmentResponse> getInstallmentsOfLoan(@PathVariable Long loanId) {
        return loanService.getInstallmentsOfLoan(loanId);
    }

    @PostMapping("{loanId}/payment") // todo: consider using put
    public List<LoanInstallmentResponse> payLoan(@PathVariable Long loanId, @RequestBody PayLoanRequest payLoanRequest) {
        return loanService.getInstallmentsOfLoan(loanId);
    }
}
