package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.dto.*;
import com.eoral.loanapi.entity.Customer;
import com.eoral.loanapi.entity.Loan;
import com.eoral.loanapi.entity.LoanInstallment;
import com.eoral.loanapi.exception.BadRequestException;
import com.eoral.loanapi.exception.NotFoundException;
import com.eoral.loanapi.repository.LoanInstallmentRepository;
import com.eoral.loanapi.repository.LoanRepository;
import com.eoral.loanapi.service.*;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefaultLoanService implements LoanService {

    private final CustomerService customerService;
    private final ValidationService validationService;
    private final DateTimeService dateTimeService;
    private final EntityDtoConversionService entityDtoConversionService;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public DefaultLoanService(
            CustomerService customerService,
            ValidationService validationService,
            DateTimeService dateTimeService,
            EntityDtoConversionService entityDtoConversionService,
            LoanRepository loanRepository,
            LoanInstallmentRepository loanInstallmentRepository) {
        this.customerService = customerService;
        this.validationService = validationService;
        this.dateTimeService = dateTimeService;
        this.entityDtoConversionService = entityDtoConversionService;
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Override
    public List<LoanResponse> getLoansOfCustomer(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        Customer customer = customerService.checkCustomer(customerId);
        customerService.checkCustomerCanBeManagedByCurrentUser(customer);
        List<Loan> loans = getLoans(customerId, numberOfInstallments, isPaid);
        return loans.stream()
                .map(e -> entityDtoConversionService.convertToLoanResponse(e))
                .collect(Collectors.toList());
    }

    private List<Loan> getLoans(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
        Customer customer = new Customer();
        customer.setId(customerId);
        Loan loanExample = new Loan();
        loanExample.setCustomer(customer);
        if (numberOfInstallments != null) {
            loanExample.setNumberOfInstallments(numberOfInstallments);
        }
        if (isPaid != null) {
            loanExample.setIsPaid(isPaid);
        }
        ExampleMatcher exampleMatcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Loan> example = Example.of(loanExample, exampleMatcher);
        // It is better to use JPA Metamodel classes when referencing entity attributes.
        return loanRepository.findAll(example, Sort.by(Sort.Direction.ASC, "createdDate"));
    }

    @Override
    @Transactional
    public LoanResponse createLoan(CreateLoanRequest createLoanRequest) {
        Customer customer = customerService.checkCustomer(createLoanRequest.customerId());
        customerService.checkCustomerCanBeManagedByCurrentUser(customer);
        BigDecimal availableCreditLimit = customer.getCreditLimit().subtract(customer.getUsedCreditLimit());
        validationService.checkAmountForCreateLoan(createLoanRequest.amount(), availableCreditLimit);
        validationService.checkNumberOfInstallmentsForCreateLoan(createLoanRequest.numberOfInstallments());
        validationService.checkInterestRateForCreateLoan(createLoanRequest.interestRate());
        Loan loan = createLoan(customer, createLoanRequest);
        createLoanInstallments(loan, createLoanRequest);
        customerService.increaseUsedCreditLimit(customer, createLoanRequest.amount());
        return entityDtoConversionService.convertToLoanResponse(loan);
    }

    private Loan createLoan(Customer customer, CreateLoanRequest createLoanRequest) {
        LocalDate currentDate = dateTimeService.getCurrentDateForCurrentBankBranch();
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(createLoanRequest.amount());
        loan.setNumberOfInstallments(createLoanRequest.numberOfInstallments());
        loan.setInterestRate(createLoanRequest.interestRate());
        loan.setStartDate(currentDate);
        loan.setIsPaid(false);
        return loanRepository.save(loan);
    }

    private List<LoanInstallment> createLoanInstallments(Loan loan, CreateLoanRequest createLoanRequest) {
        BigDecimal amountWithoutInterest = calculateLoanInstallmentAmountWithoutInterest(
                createLoanRequest.amount(), createLoanRequest.numberOfInstallments());
        BigDecimal amountWithInterest = calculateLoanInstallmentAmountWithInterest(
                createLoanRequest.amount(), createLoanRequest.numberOfInstallments(), createLoanRequest.interestRate());
        List<LoanInstallment> loanInstallments = new ArrayList<>();
        for (int installmentNo = 1; installmentNo <= createLoanRequest.numberOfInstallments(); installmentNo++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setLoan(loan);
            loanInstallment.setAmountWithoutInterest(amountWithoutInterest);
            loanInstallment.setAmount(amountWithInterest);
            loanInstallment.setDueDate(dateTimeService.calculateLoanInstallmentDueDate(loan.getStartDate(), installmentNo));
            loanInstallment.setIsPaid(false);
            loanInstallments.add(loanInstallment);
        }
        return loanInstallmentRepository.saveAll(loanInstallments);
    }

    private BigDecimal calculateLoanInstallmentAmountWithoutInterest(BigDecimal loanAmount, int numberOfInstallments) {
        return loanAmount.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.CEILING);
    }

    private BigDecimal calculateLoanInstallmentAmountWithInterest(BigDecimal loanAmount, int numberOfInstallments, BigDecimal interestRate) {
        BigDecimal loanAmountAfterInterestRateApplied = loanAmount.multiply(BigDecimal.ONE.add(interestRate));
        return loanAmountAfterInterestRateApplied.divide(BigDecimal.valueOf(numberOfInstallments), 2, RoundingMode.CEILING);
    }

    @Override
    public List<LoanInstallmentResponse> getInstallmentsOfLoan(Long loanId) {
        Loan loan = checkLoan(loanId);
        customerService.checkCustomerCanBeManagedByCurrentUser(loan.getCustomer());
        return loanInstallmentRepository.findByLoanIdOrderByDueDate(loanId).stream()
                .map(e -> entityDtoConversionService.convertToLoanInstallmentResponse(e))
                .collect(Collectors.toList());
    }

    private Loan checkLoan(Long loanId) {
        if (loanId == null) {
            throw new BadRequestException("Loan is not specified.");
        }
        Optional<Loan> optionalLoan = loanRepository.findById(loanId);
        return optionalLoan.orElseThrow(() -> new NotFoundException("Loan is not found."));
    }

    @Override
    @Transactional
    public PayLoanResponse payLoan(Long loanId, PayLoanRequest payLoanRequest) {
        Loan loan = checkLoan(loanId);
        customerService.checkCustomerCanBeManagedByCurrentUser(loan.getCustomer());
        checkLoanIsNotPaid(loan);
        checkAmountForPayLoan(payLoanRequest.amount());
        List<LoanInstallment> payableLoanInstallments = findPayableLoanInstallments(loanId, payLoanRequest.amount());
        List<LoanInstallment> paidLoanInstallments = updateLoanInstallmentsAsPaid(payableLoanInstallments);
        SummaryOfPaidLoanInstallments summary = getSummaryOfPaidLoanInstallments(paidLoanInstallments);
        boolean isPaidCompletely = updateLoanAsPaidIfApplicable(loan);
        if (summary.numberOfInstallmentsPaid() > 0) {
            customerService.decreaseUsedCreditLimit(loan.getCustomer(), summary.totalPaidAmountWithoutInterest());
        }
        return new PayLoanResponse(summary.numberOfInstallmentsPaid(), summary.totalPaidAmount(), isPaidCompletely);
    }

    private void checkLoanIsNotPaid(Loan loan) {
        if (loan.getIsPaid()) {
            throw new BadRequestException("Loan is already paid.");
        }
    }

    private void checkAmountForPayLoan(BigDecimal amount) {
        if (amount == null) {
            throw new BadRequestException("Amount is not specified.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount should be greater than 0.");
        }
    }

    private List<LoanInstallment> findPayableLoanInstallments(Long loanId, BigDecimal paymentAmount) {
        LocalDate currentDate = dateTimeService.getCurrentDateForCurrentBankBranch();
        LocalDate maxDueDate = dateTimeService.findMaxDueDateForPayableInstallments(currentDate);
        List<LoanInstallment> loanInstallments = loanInstallmentRepository.
                findByLoanIdAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDate(loanId, maxDueDate);
        BigDecimal availableAmount = paymentAmount;
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
        return payableLoanInstallments;
    }

    private List<LoanInstallment> updateLoanInstallmentsAsPaid(List<LoanInstallment> loanInstallments) {
        LocalDate paymentDate = dateTimeService.getCurrentDateForCurrentBankBranch();
        for (LoanInstallment loanInstallment : loanInstallments) {
            loanInstallment.setPaidAmount(loanInstallment.getAmount());
            loanInstallment.setPaymentDate(paymentDate);
            loanInstallment.setIsPaid(true);
        }
        return loanInstallmentRepository.saveAll(loanInstallments);
    }

    private SummaryOfPaidLoanInstallments getSummaryOfPaidLoanInstallments(List<LoanInstallment> paidLoanInstallments) {
        BigDecimal totalPaidAmountWithoutInterest = BigDecimal.ZERO;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        for (LoanInstallment loanInstallment : paidLoanInstallments) {
            totalPaidAmountWithoutInterest = totalPaidAmountWithoutInterest.add(loanInstallment.getAmountWithoutInterest());
            totalPaidAmount = totalPaidAmount.add(loanInstallment.getAmount());
        }
        return new SummaryOfPaidLoanInstallments(totalPaidAmountWithoutInterest, totalPaidAmount, paidLoanInstallments.size());
    }

    private boolean updateLoanAsPaidIfApplicable(Loan loan) {
        if (loanInstallmentRepository.countByLoanIdAndIsPaidFalse(loan.getId()) == 0) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
            return true;
        } else {
            return false;
        }
    }
}
