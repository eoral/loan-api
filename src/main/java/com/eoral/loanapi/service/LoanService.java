package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.*;

import java.util.List;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest createLoanRequest, String user);

    List<LoanResponse> getLoansOfCustomer(Long customerId, Integer numberOfInstallments, Boolean isPaid);

    List<LoanInstallmentResponse> getInstallmentsOfLoan(Long loanId);

    PayLoanResponse payLoan(Long loanId, PayLoanRequest payLoanRequest);

}
