package com.eoral.loanapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoanResponse(Long id, BigDecimal loanAmount, Integer numberOfInstallments, BigDecimal interestRate, LocalDate createDate, Boolean isPaid) {
}
