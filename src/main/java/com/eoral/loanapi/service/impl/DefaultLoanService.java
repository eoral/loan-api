package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.CreateLoanRequest;
import com.eoral.loanapi.dto.CreateLoanResponse;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.LoanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultLoanService implements LoanService {

    private static final Set<Integer> ALLOWED_NUMBER_OF_INSTALLMENTS = new HashSet<>(Arrays.asList(6, 9, 12, 24));
    private static final String ALLOWED_NUMBER_OF_INSTALLMENTS_STR = ALLOWED_NUMBER_OF_INSTALLMENTS.stream().map(String::valueOf).collect(Collectors.joining(", "));
    private static final BigDecimal MIN_INTEREST_RATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal MAX_INTEREST_RATE = BigDecimal.valueOf(0.5);

    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public DefaultLoanService(LoanRepository loanRepository, CustomerRepository customerRepository, LoanInstallmentRepository loanInstallmentRepository) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    @Transactional
    public CreateLoanResponse createLoan(CreateLoanRequest createLoanRequest) {

        // Long customerId, BigDecimal amount, Integer numberOfInstallments, BigDecimal interestRate

        if (createLoanRequest.customerId() == null) {
            throw new BadRequestException("Customer is not specified.");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(createLoanRequest.customerId());
        Customer customer = optionalCustomer.orElseThrow(() -> new NotFoundException("Customer is not found."));
        BigDecimal availableCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());

        if (createLoanRequest.amount() == null) {
            throw new BadRequestException("Amount is not specified.");
        }

        if (createLoanRequest.amount().compareTo(availableCreditLimit) > 0) {
            throw new BadRequestException("Amount exceeds available amount.");
        }

        if (createLoanRequest.numberOfInstallments() == null) {
            throw new BadRequestException("Number of installments is not specified.");
        }

        if (!ALLOWED_NUMBER_OF_INSTALLMENTS.contains(createLoanRequest.numberOfInstallments())) {
            throw new BadRequestException("Number of installments should be one of " + ALLOWED_NUMBER_OF_INSTALLMENTS_STR);
        }

        if (!isInterestRateInAllowedRange(createLoanRequest.interestRate())) {
            throw new BadRequestException("Interest rate is not in allowed range.");
        }

        //  id, customerId, loanAmount, numberOfInstallment, createDate, isPaid
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(createLoanRequest.amount());
        loan.setNumberOfInstallments(createLoanRequest.numberOfInstallments());
        loan.setCreateDate(getCurrentDate());
        loan.setPaid(false);

        Loan persistedLoan = loanRepository.save(loan);
        createLoanInstallments(persistedLoan, createLoanRequest);

        return new CreateLoanResponse(persistedLoan.getId());
    }


    private boolean isInterestRateInAllowedRange(BigDecimal interestRate) {
        return (interestRate.compareTo(MIN_INTEREST_RATE) >= 0 && interestRate.compareTo(MAX_INTEREST_RATE) <= 0);
    }

    private LocalDate getCurrentDate() {
        // I am using LocalDate.now() for simplicity. If we have customers in different time zones or countries,
        // it is better to consider time zones.
        return LocalDate.now();
    }

    private List<LoanInstallment> createLoanInstallments(Loan loan, CreateLoanRequest createLoanRequest) {

        // id, loanId, amount, paidAmount, dueDate, paymentDate, isPaid

        BigDecimal loanInstallmentAmount = calculateLoanInstallmentAmount(
                createLoanRequest.amount(), createLoanRequest.numberOfInstallments(), createLoanRequest.interestRate());

        List<LoanInstallment> loanInstallments = new ArrayList<>();

        for (int installmentNo = 1; installmentNo <= createLoanRequest.numberOfInstallments(); installmentNo++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setLoan(loan);
            loanInstallment.setAmount(loanInstallmentAmount);
            loanInstallment.setDueDate(calculateLoanInstallmentDueDate(loan.getCreateDate(), installmentNo));
            loanInstallment.setPaid(false);
            loanInstallments.add(loanInstallment);
        }

        return loanInstallmentRepository.saveAll(loanInstallments);
    }

    private BigDecimal calculateLoanInstallmentAmount(BigDecimal loanAmount, int numberOfInstallments, BigDecimal interestRate) {
        BigDecimal loanAmountAfterInterestRateApplied = loanAmount.multiply(BigDecimal.ONE.add(interestRate));
        return loanAmountAfterInterestRateApplied.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.CEILING);
    }

    private LocalDate calculateLoanInstallmentDueDate(LocalDate loanDate, int installmentNo) {
        return loanDate.plusMonths(installmentNo).withDayOfMonth(1);
    }
}
