package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.dto.GetLoansOfCustomerRequest;
import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> getAllLoans();

    LoanResponse createLoan(CreateLoanRequest createLoanRequest);

    List<LoanResponse> getLoansOfCustomer(GetLoansOfCustomerRequest getLoansOfCustomerRequest);

}
