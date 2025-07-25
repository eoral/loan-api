package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.service.EntityDtoConversionService;
import org.springframework.stereotype.Service;

@Service
public class DefaultEntityDtoConversionService implements EntityDtoConversionService {

    @Override
    public LoanResponse convertToLoanResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getLoanAmount(),
                loan.getNumberOfInstallments(),
                loan.getInterestRate(),
                loan.getCreateDate(),
                loan.getPaid());
    }
}
