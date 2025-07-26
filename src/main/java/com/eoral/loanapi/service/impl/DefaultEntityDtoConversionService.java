package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CustomerResponse;
import com.eoral.loanapi.dto.LoanInstallmentResponse;
import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.service.EntityDtoConversionService;
import org.springframework.stereotype.Service;

@Service
public class DefaultEntityDtoConversionService implements EntityDtoConversionService {

    @Override
    public CustomerResponse convertToCustomerResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getSurname(),
                customer.getCreditLimit(),
                customer.getUsedCreditLimit());
    }

    @Override
    public LoanResponse convertToLoanResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanAmount(),
                loan.getNumberOfInstallments(),
                loan.getInterestRate(),
                loan.getStartDate(),
                loan.getIsPaid());
    }

    @Override
    public LoanInstallmentResponse convertToLoanInstallmentResponse(LoanInstallment loanInstallment) {
        return new LoanInstallmentResponse(
                loanInstallment.getId(),
                loanInstallment.getAmountWithoutInterest(),
                loanInstallment.getAmount(),
                loanInstallment.getPaidAmount(),
                loanInstallment.getDueDate(),
                loanInstallment.getPaymentDate(),
                loanInstallment.getPaid());
    }
}
