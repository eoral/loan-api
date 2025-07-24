package com.eoral.loanapi.controller;

import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.service.LoanInstallmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/loan-installments")
public class LoanInstallmentController {

    private final LoanInstallmentService loanInstallmentService;

    public LoanInstallmentController(LoanInstallmentService loanInstallmentService) {
        this.loanInstallmentService = loanInstallmentService;
    }

    @GetMapping("")
    public List<LoanInstallment> getAllLoanInstallments() {
        return loanInstallmentService.getAllLoanInstallments();
    }
}
