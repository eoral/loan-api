package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.CustomerService;
import com.eoral.loanapi.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultLoanServiceTest {

    @Mock
    private CustomerService customerService;
    @Mock
    private ValidationService validationService;
    @Spy
    private DefaultDateTimeService dateTimeService; // Use actual implementation instead of a mock
    @Spy
    private DefaultEntityDtoConversionService entityDtoConversionService; // Use actual implementation instead of a mock
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;
    @InjectMocks
    private DefaultLoanService defaultLoanService;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100000);
    private static final BigDecimal DEFAULT_USED_CREDIT_LIMIT = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_AMOUNT_FOR_CREATE_LOAN = BigDecimal.valueOf(50000);
    private static final Integer DEFAULT_NUMBER_OF_INSTALLMENTS_FOR_CREATE_LOAN = 6;
    private static final BigDecimal DEFAULT_INTEREST_RATE_FOR_CREATE_LOAN = new BigDecimal("0.5");

    private static Customer newCustomer() {
        Customer customer = new Customer();
        customer.setId(DEFAULT_CUSTOMER_ID);
        customer.setCreditLimit(DEFAULT_CREDIT_LIMIT);
        customer.setUsedCreditLimit(DEFAULT_USED_CREDIT_LIMIT);
        return customer;
    }

    private static CreateLoanRequest newCreateLoanRequest() {
        return new CreateLoanRequest(
                DEFAULT_CUSTOMER_ID,
                DEFAULT_AMOUNT_FOR_CREATE_LOAN,
                DEFAULT_NUMBER_OF_INSTALLMENTS_FOR_CREATE_LOAN,
                DEFAULT_INTEREST_RATE_FOR_CREATE_LOAN);
    }

    @Test
    public void createLoanShouldSucceed() {
        Customer customer = newCustomer();
        BigDecimal availableCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        CreateLoanRequest createLoanRequest = newCreateLoanRequest();
        when(customerService.checkCustomer(createLoanRequest.customerId())).thenReturn(customer);
        doNothing().when(customerService).checkCustomerCanBeManagedByCurrentUser(customer);
        doNothing().when(validationService).checkAmountForCreateLoan(createLoanRequest.amount(), availableCreditLimit);
        doNothing().when(validationService).checkNumberOfInstallmentsForCreateLoan(createLoanRequest.numberOfInstallments());
        doNothing().when(validationService).checkInterestRateForCreateLoan(createLoanRequest.interestRate());
        when(loanRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(loanInstallmentRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(customerService).increaseUsedCreditLimit(any(), any());
        defaultLoanService.createLoan(createLoanRequest);
    }
}
