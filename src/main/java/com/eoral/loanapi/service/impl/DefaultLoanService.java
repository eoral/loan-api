package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.*;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.repository.CustomerRepository;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.EntityDtoConversionService;
import com.eoral.loanapi.service.LoanService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final EntityDtoConversionService entityDtoConversionService;

    public DefaultLoanService(LoanRepository loanRepository, CustomerRepository customerRepository, LoanInstallmentRepository loanInstallmentRepository, EntityDtoConversionService entityDtoConversionService) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.entityDtoConversionService = entityDtoConversionService;
    }

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    @Transactional
    public LoanResponse createLoan(CreateLoanRequest createLoanRequest) {

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

        if (createLoanRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount should be greater than 0.");
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

        LocalDate currentDate = getCurrentDate();

        //  id, customerId, loanAmount, numberOfInstallment, createDate, isPaid
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(createLoanRequest.amount());
        loan.setNumberOfInstallments(createLoanRequest.numberOfInstallments());
        loan.setInterestRate(createLoanRequest.interestRate());
        loan.setCreateDate(currentDate);
        loan.setPaid(false);

        Loan persistedLoan = loanRepository.save(loan);
        createLoanInstallments(persistedLoan, createLoanRequest);

        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().add(createLoanRequest.amount());
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);

        return entityDtoConversionService.convertToLoanResponse(persistedLoan);
    }


    private boolean isInterestRateInAllowedRange(BigDecimal interestRate) {
        return (interestRate.compareTo(MIN_INTEREST_RATE) >= 0 && interestRate.compareTo(MAX_INTEREST_RATE) <= 0);
    }

    private LocalDate getCurrentDate() {
        // I am using LocalDate.now() for simplicity. Normally, we should consider customer time zone.
        return LocalDate.now();
    }

    private List<LoanInstallment> createLoanInstallments(Loan loan, CreateLoanRequest createLoanRequest) {

        // id, loanId, amountWithoutInterest, amount, paidAmount, dueDate, paymentDate, isPaid

        BigDecimal loanInstallmentAmountWithoutInterest = calculateLoanInstallmentAmountWithoutInterest(
                createLoanRequest.amount(), createLoanRequest.numberOfInstallments());

        BigDecimal loanInstallmentAmount = calculateLoanInstallmentAmount(
                createLoanRequest.amount(), createLoanRequest.numberOfInstallments(), createLoanRequest.interestRate());

        List<LoanInstallment> loanInstallments = new ArrayList<>();

        for (int installmentNo = 1; installmentNo <= createLoanRequest.numberOfInstallments(); installmentNo++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setLoan(loan);
            loanInstallment.setAmountWithoutInterest(loanInstallmentAmountWithoutInterest);
            loanInstallment.setAmount(loanInstallmentAmount);
            loanInstallment.setDueDate(calculateLoanInstallmentDueDate(loan.getCreateDate(), installmentNo));
            loanInstallment.setPaid(false);
            loanInstallments.add(loanInstallment);
        }

        return loanInstallmentRepository.saveAll(loanInstallments);
    }

    private BigDecimal calculateLoanInstallmentAmountWithoutInterest(BigDecimal loanAmount, int numberOfInstallments) {
        return loanAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.CEILING);
    }

    private BigDecimal calculateLoanInstallmentAmount(BigDecimal loanAmount, int numberOfInstallments, BigDecimal interestRate) {
        BigDecimal loanAmountAfterInterestRateApplied = loanAmount.multiply(BigDecimal.ONE.add(interestRate));
        return loanAmountAfterInterestRateApplied.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.CEILING);
    }

    private LocalDate calculateLoanInstallmentDueDate(LocalDate loanDate, int installmentNo) {
        return loanDate.plusMonths(installmentNo).withDayOfMonth(1);
    }

    @Override
    public List<LoanResponse> getLoansOfCustomer(GetLoansOfCustomerRequest getLoansOfCustomerRequest) {

        if (getLoansOfCustomerRequest.customerId() == null) {
            throw new BadRequestException("Customer is not specified.");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(getLoansOfCustomerRequest.customerId());
        Customer customer = optionalCustomer.orElseThrow(() -> new NotFoundException("Customer is not found."));

        Loan loanExample = new Loan();
        loanExample.setCustomer(customer);
        if (getLoansOfCustomerRequest.numberOfInstallments() != null) {
            loanExample.setNumberOfInstallments(getLoansOfCustomerRequest.numberOfInstallments());
        }
        if (getLoansOfCustomerRequest.isPaid() != null) {
            loanExample.setPaid(getLoansOfCustomerRequest.isPaid());
        }

        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Loan> example = Example.of(loanExample, exampleMatcher);

        // todo: sort
        return loanRepository.findAll(example).stream()
                .map(e -> entityDtoConversionService.convertToLoanResponse(e))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanInstallmentResponse> getInstallmentsOfLoan(Long loanId) {
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        if (optionalLoan.isEmpty()) {
            throw new NotFoundException("Loan is not found.");
        }
        return loanInstallmentRepository.findByLoanIdOrderByDueDate(loanId).stream()
                .map(e -> entityDtoConversionService.convertToLoanInstallmentResponse(e))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PayLoanResponse payLoan(Long loanId, PayLoanRequest payLoanRequest) {

        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        Loan loan = optionalLoan.orElseThrow(() -> new NotFoundException("Loan is not found."));

        if (loan.getPaid()) {
            throw new BadRequestException("Loan is already paid.");
        }

        if (payLoanRequest.amount() == null) {
            throw new BadRequestException("Amount is not specified.");
        }

        if (payLoanRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount should be greater than 0.");
        }

        Optional<Customer> optionalCustomer = customerRepository.findById(loan.getCustomer().getId());
        Customer customer = optionalCustomer.get();
        LocalDate currentDate = getCurrentDate();
        LocalDate maxDueDate = findMaxDueDateForPayableInstallments(currentDate);
        List<LoanInstallment> loanInstallments = loanInstallmentRepository.findByLoanIdAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDate(loanId, maxDueDate);
        BigDecimal availableAmount = payLoanRequest.amount().add(BigDecimal.ZERO);
        List<LoanInstallment> payableLoanInstallments = new ArrayList<>();

        for (LoanInstallment loanInstallment : loanInstallments) {
            int compareToResult = loanInstallment.getAmount().compareTo(availableAmount);
            if (compareToResult <= 0) {
                payableLoanInstallments.add(loanInstallment);
                availableAmount = availableAmount.subtract(loanInstallment.getAmount());
            }
            if (compareToResult >= 0) {
                break;
            }
        }

        BigDecimal totalPaidAmountWithoutInterest = BigDecimal.ZERO;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        for (LoanInstallment loanInstallment : payableLoanInstallments) {
            loanInstallment.setPaidAmount(loanInstallment.getAmount());
            loanInstallment.setPaymentDate(currentDate);
            loanInstallment.setPaid(true);
            totalPaidAmountWithoutInterest = totalPaidAmountWithoutInterest.add(loanInstallment.getAmountWithoutInterest());
            totalPaidAmount = totalPaidAmount.add(loanInstallment.getAmount());
        }
        loanInstallmentRepository.saveAll(payableLoanInstallments);
        int numberOfInstallmentsPaid = payableLoanInstallments.size();

        boolean isPaidCompletely = false;
        if (loanInstallmentRepository.countByLoanIdAndIsPaidFalse(loanId) == 0) {
            loan.setPaid(true);
            loanRepository.save(loan);
            isPaidCompletely = true;
        }

        BigDecimal newUsedCreditLimit = customer.getUsedCreditLimit().subtract(totalPaidAmountWithoutInterest);
        customer.setUsedCreditLimit(newUsedCreditLimit);
        customerRepository.save(customer);

        return new PayLoanResponse(numberOfInstallmentsPaid, totalPaidAmount, isPaidCompletely);
    }

    private LocalDate findMaxDueDateForPayableInstallments(LocalDate currentDate) {
        return currentDate.withDayOfMonth(1).plusMonths(3).minusDays(1);
    }
}
