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
import java.util.concurrent.atomic.AtomicLong;

import static com.eoral.loanapi.TestUtils.assertBigDecimalsAreEqual;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private static final AtomicLong LOAN_ID_GENERATOR = new AtomicLong(0);
    private static final AtomicLong LOAN_INSTALLMENT_ID_GENERATOR = new AtomicLong(0);

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
        when(loanRepository.save(any())).thenAnswer(i -> {
            Loan arg0 = (Loan) i.getArguments()[0];
            arg0.setId(LOAN_ID_GENERATOR.incrementAndGet());
            return arg0;
        });
        when(loanInstallmentRepository.saveAll(any())).thenAnswer(i -> {
            List<LoanInstallment> arg0 = (List<LoanInstallment>) i.getArguments()[0];
            arg0.stream().forEach(item -> item.setId(LOAN_INSTALLMENT_ID_GENERATOR.incrementAndGet()));
            return arg0;
        });
        doNothing().when(customerService).increaseUsedCreditLimit(customer, createLoanRequest.amount());
        return defaultLoanService.createLoan(createLoanRequest);
    }

    private Loan captureLoan() {
        verify(loanRepository).save(loanCaptor.capture());
        return loanCaptor.getValue();
    }

    private List<LoanInstallment> captureLoanInstallments() {
        verify(loanInstallmentRepository).saveAll(loanInstallmentsCaptor.capture());
        return loanInstallmentsCaptor.getValue();
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
        Loan loan = captureLoan();
        assertNotNull(loan.getId());
        assertEquals(createLoanRequest.customerId(), loan.getCustomer().getId());
        assertBigDecimalsAreEqual(createLoanRequest.amount(), loan.getLoanAmount());
        assertEquals(createLoanRequest.numberOfInstallments(), loan.getNumberOfInstallments());
        assertBigDecimalsAreEqual(createLoanRequest.interestRate(), loan.getInterestRate());
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(Boolean.FALSE, loan.getIsPaid());
    }

    @Test
    public void createLoanShouldCreateLoanInstallmentEntityListWithExpectedSize() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
        List<LoanInstallment> loanInstallments = captureLoanInstallments();
        assertEquals(createLoanRequest.numberOfInstallments(), loanInstallments.size());
    }

    @Test
    public void createLoanShouldCreateLoanInstallmentEntityListWithExpectedValues() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        createLoan(customer, createLoanRequest);
        Loan loan = captureLoan();
        List<LoanInstallment> loanInstallments = captureLoanInstallments();
        int installmentNo = 0;
        for (LoanInstallment loanInstallment : loanInstallments) {
            installmentNo++;
            assertNotNull(loanInstallment.getId());
            assertEquals(loan.getId(), loanInstallment.getLoan().getId());
            assertBigDecimalsAreEqual(BigDecimal.valueOf(10), loanInstallment.getAmountWithoutInterest());
            assertBigDecimalsAreEqual(BigDecimal.valueOf(15), loanInstallment.getAmount());
            assertNull(loanInstallment.getPaidAmount());
            assertEquals(dateTimeService.calculateLoanInstallmentDueDate(LocalDate.now(), installmentNo), loanInstallment.getDueDate());
            assertNull(loanInstallment.getPaymentDate());
            assertEquals(Boolean.FALSE, loanInstallment.getIsPaid());
        }
    }

    @Test
    public void createLoanShouldReturnResponseWithExpectedValues() {
        Customer customer = newCustomer();
        CreateLoanRequest createLoanRequest = newCreateLoanRequest(customer);
        LoanResponse loanResponse = createLoan(customer, createLoanRequest);
        Loan loan = captureLoan();
        assertEquals(loan.getId(), loanResponse.id());
        assertEquals(loan.getLoanAmount(), loanResponse.loanAmount());
        assertEquals(loan.getNumberOfInstallments(), loanResponse.numberOfInstallments());
        assertBigDecimalsAreEqual(loan.getInterestRate(), loanResponse.interestRate());
        assertEquals(loan.getStartDate(), loanResponse.startDate());
        assertEquals(loan.getIsPaid(), loanResponse.isPaid());
    }
}
