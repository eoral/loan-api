package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.model.LoanInstallment;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.service.LoanInstallmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultLoanInstallmentService implements LoanInstallmentService {

    private final LoanInstallmentRepository loanInstallmentRepository;

    public DefaultLoanInstallmentService(LoanInstallmentRepository loanInstallmentRepository) {
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Override
    public List<LoanInstallment> getAllLoanInstallments() {
        return loanInstallmentRepository.findAll();
    }
}
