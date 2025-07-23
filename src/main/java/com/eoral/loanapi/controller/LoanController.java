package com.eoral.loanapi.controller;

import com.eoral.loanapi.model.Loan;
import com.eoral.loanapi.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
