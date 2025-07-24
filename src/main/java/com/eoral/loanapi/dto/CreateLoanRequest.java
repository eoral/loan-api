package com.eoral.loanapi.dto;

import java.math.BigDecimal;

public record CreateLoanRequest (Long customerId, BigDecimal amount, Integer numberOfInstallments, BigDecimal interestRate) {
}
