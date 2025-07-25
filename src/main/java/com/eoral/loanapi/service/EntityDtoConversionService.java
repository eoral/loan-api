package com.eoral.loanapi.service;

import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Loan;

public interface EntityDtoConversionService {

    LoanResponse convertToLoanResponse(Loan loan);

}
