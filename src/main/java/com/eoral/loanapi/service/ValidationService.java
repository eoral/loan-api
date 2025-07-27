package com.eoral.loanapi.service;

import java.math.BigDecimal;

public interface ValidationService {

    void checkAmountForCreateLoan(BigDecimal amount, BigDecimal availableCreditLimit);

    void checkNumberOfInstallmentsForCreateLoan(Integer numberOfInstallments);

    void checkInterestRateForCreateLoan(BigDecimal interestRate);

}
