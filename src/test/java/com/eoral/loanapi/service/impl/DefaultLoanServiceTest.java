package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.dto.LoanResponse;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.CustomerService;
import com.eoral.loanapi.service.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static com.eoral.loanapi.TestUtils.*;

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
    @Captor
    private ArgumentCaptor<Loan> loanCaptor;
    @Captor
    private ArgumentCaptor<List<LoanInstallment>> loanInstallmentsCaptor;

    private static final Long DEFAULT_CUSTOMER_ID = 1L;
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100);
    private static final BigDecimal DEFAULT_USED_CREDIT_LIMIT = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_AMOUNT_FOR_CREATE_LOAN = BigDecimal.valueOf(60);
    private static final Integer DEFAULT_NUMBER_OF_INSTALLMENTS_FOR_CREATE_LOAN = 6;
    private static final BigDecimal DEFAULT_INTEREST_RATE_FOR_CREATE_LOAN = new BigDecimal("0.5");

    private static Customer newCustomer() {
        Customer customer = new Customer();
        customer.setId(DEFAULT_CUSTOMER_ID);
        customer.setCreditLimit(DEFAULT_CREDIT_LIMIT);
        customer.setUsedCreditLimit(DEFAULT_USED_CREDIT_LIMIT);
        return customer;
    }

    private static CreateLoanRequest newCreateLoanRequest(Customer customer) {
        return new CreateLoanRequest(
                customer.getId(),
                DEFAULT_AMOUNT_FOR_CREATE_LOAN,
                DEFAULT_NUMBER_OF_INSTALLMENTS_FOR_CREATE_LOAN,
                DEFAULT_INTEREST_RATE_FOR_CREATE_LOAN);
    }

    private LoanResponse createLoan(Customer customer, CreateLoanRequest createLoanRequest) {
        BigDecimal availableCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        when(customerService.checkCustomer(createLoanRequest.customerId())).thenReturn(customer);
        doNothing().when(customerService).checkCustomerCanBeManagedByCurrentUser(customer);
        doNothing().when(validationService).checkAmountForCreateLoan(createLoanRequest.amount(), availableCreditLimit);
        doNothing().when(validationService).checkNumberOfInstallmentsForCreateLoan(createLoanRequest.numberOfInstallments());
        doNothing().when(validationService).checkInterestRateForCreateLoan(createLoanRequest.interestRate());
        when(loanRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(loanInstallmentRepository.saveAll(any())).thenAnswer(i -> i.getArguments()[0]);
        doNothing().when(customerService).increaseUsedCreditLimit(customer, createLoanRequest.amount());
        return defaultLoanService.createLoan(createLoanRequest);
    }

    @Test
    public void createLoanShouldSucceed() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
    }

    @Test
    public void createLoanShouldCreateLoanEntityWithExpectedValues() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
        verify(loanRepository).save(loanCaptor.capture());
        Loan loan = loanCaptor.getValue();
        assertEquals(createLoanRequest.customerId(), loan.getCustomer().getId());
        assertBigDecimalsAreEqual(createLoanRequest.amount(), loan.getLoanAmount());
        assertEquals(createLoanRequest.numberOfInstallments(), loan.getNumberOfInstallments());
        assertBigDecimalsAreEqual(createLoanRequest.interestRate(), loan.getInterestRate());
        assertEquals(LocalDate.now(), loan.getStartDate()); // todo: is it correct
        assertEquals(Boolean.FALSE, loan.getIsPaid());
    }

    @Test
    public void createLoanShouldCreateLoanInstallmentEntityListWithCorrectSize() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
        verify(loanInstallmentRepository).saveAll(loanInstallmentsCaptor.capture());
        List<LoanInstallment> loanInstallments = loanInstallmentsCaptor.getValue();
        assertEquals(createLoanRequest.numberOfInstallments(), loanInstallments.size());
    }

    @Test
    public void createLoanShouldCreateLoanInstallmentEntityListWithExpectedValues() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
        verify(loanInstallmentRepository).saveAll(loanInstallmentsCaptor.capture());
        List<LoanInstallment> loanInstallments = loanInstallmentsCaptor.getValue();
        for (LoanInstallment loanInstallment : loanInstallments) {
            assertNotNull(loanInstallment.getLoan());
            assertBigDecimalsAreEqual(BigDecimal.valueOf(10), loanInstallment.getAmountWithoutInterest());
            assertBigDecimalsAreEqual(BigDecimal.valueOf(15), loanInstallment.getAmount());
            assertNull(loanInstallment.getPaidAmount());
            assertNotNull(loanInstallment.getDueDate());
            assertNull(loanInstallment.getPaymentDate());
            assertEquals(Boolean.FALSE, loanInstallment.getIsPaid());
        }
    }
}
