package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DefaultValidationServiceTest {

    @InjectMocks
    private DefaultValidationService defaultValidationService;

    private void checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsInvalid(
            BigDecimal amount, BigDecimal availableCreditLimit, String expectedErrorMessage) {
        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> defaultValidationService.checkAmountForCreateLoan(amount, availableCreditLimit));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsNull() {
        checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsInvalid(
                null, BigDecimal.valueOf(100), "Amount is not specified.");
    }

    @Test
    public void checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsLessThanZero() {
        checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsInvalid(
                BigDecimal.valueOf(-1), BigDecimal.valueOf(100), "Amount should be greater than 0.");
    }

    @Test
    public void checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsZero() {
        checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsInvalid(
                BigDecimal.ZERO, BigDecimal.valueOf(100), "Amount should be greater than 0.");
    }

    @Test
    public void checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsGreaterThanAvailableCreditLimit() {
        checkAmountForCreateLoanShouldThrowExceptionWhenAmountIsInvalid(
                BigDecimal.valueOf(101), BigDecimal.valueOf(100), "Amount exceeds available credit limit.");
    }

    @Test
    public void checkAmountForCreateLoanShouldSucceedWhenAmountIsLessThanAvailableCreditLimit() {
        defaultValidationService.checkAmountForCreateLoan(BigDecimal.valueOf(50), BigDecimal.valueOf(100));
    }

    @Test
    public void checkAmountForCreateLoanShouldSucceedWhenAmountIsEqualToAvailableCreditLimit() {
        defaultValidationService.checkAmountForCreateLoan(BigDecimal.valueOf(100), BigDecimal.valueOf(100));
    }

    private void checkNumberOfInstallmentsForCreateLoanShouldThrowExceptionWhenNumberOfInstallmentsIsInvalid(
            Integer numberOfInstallments, String expectedErrorMessage) {
        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> defaultValidationService.checkNumberOfInstallmentsForCreateLoan(numberOfInstallments));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void checkNumberOfInstallmentsForCreateLoanShouldThrowExceptionWhenNumberOfInstallmentsIsNull() {
        checkNumberOfInstallmentsForCreateLoanShouldThrowExceptionWhenNumberOfInstallmentsIsInvalid(
                null, "Number of installments is not specified.");
    }

    @Test
    public void checkNumberOfInstallmentsForCreateLoanShouldThrowExceptionWhenNumberOfInstallmentsIsNotOneOfAllowedValues() {
        checkNumberOfInstallmentsForCreateLoanShouldThrowExceptionWhenNumberOfInstallmentsIsInvalid(
                1, "Number of installments should be one of 6, 9, 12, 24");
    }

    @Test
    public void checkNumberOfInstallmentsForCreateLoanShouldSucceedForAllowedValues() {
        for (Integer value : Constants.ALLOWED_NUMBER_OF_INSTALLMENTS) {
            defaultValidationService.checkNumberOfInstallmentsForCreateLoan(value);
        }
    }

    private void checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsInvalid(
            BigDecimal interestRate, String expectedErrorMessage) {
        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> defaultValidationService.checkInterestRateForCreateLoan(interestRate));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsNull() {
        checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsInvalid(
                null, "Interest rate is not specified.");
    }

    @Test
    public void checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsLessThanMinValue() {
        checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsInvalid(
                Constants.MIN_INTEREST_RATE.subtract(new BigDecimal("0.01")),
                "Interest rate should be between 0.1 - 0.5");
    }

    @Test
    public void checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsGreaterThanMaxValue() {
        checkInterestRateForCreateLoanShouldThrowExceptionWhenInterestRateIsInvalid(
                Constants.MAX_INTEREST_RATE.add(new BigDecimal("0.01")),
                "Interest rate should be between 0.1 - 0.5");
    }

    @Test
    public void checkInterestRateForCreateLoanShouldSucceedWhenInterestRateIsEqualToMinValue() {
        defaultValidationService.checkInterestRateForCreateLoan(Constants.MIN_INTEREST_RATE);
    }

    @Test
    public void checkInterestRateForCreateLoanShouldSucceedWhenInterestRateIsEqualToMaxValue() {
        defaultValidationService.checkInterestRateForCreateLoan(Constants.MAX_INTEREST_RATE);
    }

    @Test
    public void checkInterestRateForCreateLoanShouldSucceedWhenInterestRateIsInAllowedRange() {
        defaultValidationService.checkInterestRateForCreateLoan(Constants.MIN_INTEREST_RATE.add(new BigDecimal("0.01")));
    }
}
