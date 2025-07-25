package com.eoral.loanapi.repository;

import com.eoral.loanapi.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {

    List<LoanInstallment> findByLoanIdOrderByDueDate(Long loanId);

    List<LoanInstallment> findByLoanIdAndIsPaidFalseAndDueDateLessThanEqualOrderByDueDate(Long loanId, LocalDate maxDueDate);

    long countByLoanIdAndIsPaidFalse(Long loanId);

}
