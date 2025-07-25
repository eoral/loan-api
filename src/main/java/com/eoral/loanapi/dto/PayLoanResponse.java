package com.eoral.loanapi.dto;

import java.math.BigDecimal;

public record PayLoanResponse(Integer numberOfInstallmentsPaid, BigDecimal totalAmountSpent, Boolean isPaidCompletely) {
}
