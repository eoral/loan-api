package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.dto.CreateLoanResponse;
import com.eoral.loanapi.entity.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> getAllLoans();

    CreateLoanResponse createLoan(CreateLoanRequest createLoanRequest);

}
