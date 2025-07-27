package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.service.ValidationService;
import com.eoral.loanapi.util.Constants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DefaultValidationService implements ValidationService {

    @Override
    public void checkAmountForCreateLoan(BigDecimal amount, BigDecimal availableCreditLimit) {
        if (amount == null) {
            throw new BadRequestException("Amount is not specified.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount should be greater than 0.");
        }
        if (amount.compareTo(availableCreditLimit) > 0) {
            throw new BadRequestException("Amount exceeds available credit limit.");
        }
    }

    @Override
    public void checkNumberOfInstallmentsForCreateLoan(Integer numberOfInstallments) {
        if (numberOfInstallments == null) {
            throw new BadRequestException("Number of installments is not specified.");
        }
        if (!Constants.ALLOWED_NUMBER_OF_INSTALLMENTS.contains(numberOfInstallments)) {
            throw new BadRequestException("Number of installments should be one of " + Constants.ALLOWED_NUMBER_OF_INSTALLMENTS_STR);
        }
    }

    @Override
    public void checkInterestRateForCreateLoan(BigDecimal interestRate) {
        if (interestRate == null) {
            throw new BadRequestException("Interest rate is not specified.");
        }
        if (interestRate.compareTo(Constants.MIN_INTEREST_RATE) < 0 || interestRate.compareTo(Constants.MAX_INTEREST_RATE) > 0) {
            throw new BadRequestException("Interest rate should be between " + Constants.INTEREST_RATE_RANGE_STR);
        }
    }
}
