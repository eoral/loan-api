package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.*;
import com.eoral.loanapi.entity.Loan;

import java.util.List;

public interface LoanService {

    List<Loan> getAllLoans();

    LoanResponse createLoan(CreateLoanRequest createLoanRequest);

    List<LoanResponse> getLoansOfCustomer(GetLoansOfCustomerRequest getLoansOfCustomerRequest);

    List<LoanInstallmentResponse> getInstallmentsOfLoan(Long loanId);

    PayLoanResponse payLoan(Long loanId, PayLoanRequest payLoanRequest);

}
