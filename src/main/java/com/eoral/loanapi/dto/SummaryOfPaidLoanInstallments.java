package com.eoral.loanapi.dto;

import java.math.BigDecimal;

public record SummaryOfPaidLoanInstallments(BigDecimal totalPaidAmountWithoutInterest, BigDecimal totalPaidAmount, Integer numberOfInstallmentsPaid) {
}
