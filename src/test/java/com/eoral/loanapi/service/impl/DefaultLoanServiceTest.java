package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.CustomerService;
import com.eoral.loanapi.service.DateTimeService;
import com.eoral.loanapi.service.EntityDtoConversionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultLoanServiceTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private DateTimeService dateTimeService;
    @Mock
    private EntityDtoConversionService entityDtoConversionService;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;
    @InjectMocks
    private DefaultLoanService defaultLoanService;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100000);
    private static final BigDecimal DEFAULT_USED_CREDIT_LIMIT = BigDecimal.ZERO;

    private static Customer newCustomer() {
        Customer customer = new Customer();
        customer.setId(DEFAULT_CUSTOMER_ID);
        customer.setCreditLimit(DEFAULT_CREDIT_LIMIT);
        customer.setUsedCreditLimit(DEFAULT_USED_CREDIT_LIMIT);
        return customer;
    }

    private static CreateLoanRequest newCreateLoanRequest(
            Long customerId, BigDecimal amount, Integer numberOfInstallments, BigDecimal interestRate) {
        return new CreateLoanRequest(customerId, amount, numberOfInstallments, interestRate);
    }

    private void createLoanShouldThrowExceptionWhenAmountIsInvalid(BigDecimal amount, String expectedErrorMessage) {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer.getId(), amount, 6, new BigDecimal("0.5"));
        when(customerService.checkCustomer(customer.getId())).thenReturn(customer);
        doNothing().when(customerService).checkCustomerCanBeManagedByCurrentUser(customer);
        RuntimeException exception = assertThrows(BadRequestException.class,
                () -> defaultLoanService.createLoan(createLoanRequest));
        assertEquals(expectedErrorMessage, exception.getMessage());
    }

    @Test
    public void createLoanShouldThrowExceptionWhenAmountIsNull() {
        createLoanShouldThrowExceptionWhenAmountIsInvalid(null, "Amount is not specified.");
    }

    @Test
    public void createLoanShouldThrowExceptionWhenAmountIsLessThanZero() {
        createLoanShouldThrowExceptionWhenAmountIsInvalid(BigDecimal.valueOf(-1), "Amount should be greater than 0.");
    }

    @Test
    public void createLoanShouldThrowExceptionWhenAmountIsZero() {
        createLoanShouldThrowExceptionWhenAmountIsInvalid(BigDecimal.ZERO, "Amount should be greater than 0.");
    }

    @Test
    public void createLoanShouldThrowExceptionWhenAmountIsGreaterThanAvailableCreditLimit() {
        BigDecimal availableCreditLimit = DEFAULT_CREDIT_LIMIT.subtract(DEFAULT_USED_CREDIT_LIMIT);
        BigDecimal amountThanExceedsAvailableCreditLimit = availableCreditLimit.add(BigDecimal.ONE);
        createLoanShouldThrowExceptionWhenAmountIsInvalid(amountThanExceedsAvailableCreditLimit, "Amount exceeds available credit limit.");
    }
}
