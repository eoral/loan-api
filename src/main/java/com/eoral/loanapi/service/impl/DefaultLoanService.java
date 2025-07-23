package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.model.Loan;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.LoanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultLoanService implements LoanService {

    private final LoanRepository loanRepository;

    public DefaultLoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }
}
