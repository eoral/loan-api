package com.eoral.loanapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanInstallmentResponse(Long id, BigDecimal amountWithoutInterest, BigDecimal amount, BigDecimal paidAmount, LocalDate dueDate, LocalDate paymentDate, Boolean isPaid) {
}
