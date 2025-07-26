package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.dto.LoanInstallmentResponse;
import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;

public interface EntityDtoConversionService {

    CustomerResponse convertToCustomerResponse(Customer customer);

    LoanResponse convertToLoanResponse(Loan loan);

    LoanInstallmentResponse convertToLoanInstallmentResponse(LoanInstallment loanInstallment);

}
