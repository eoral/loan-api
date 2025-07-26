package com.eoral.loanapi.dto;

import java.math.BigDecimal;

public record CustomerResponse(Long id, String name, String surname, BigDecimal creditLimit, BigDecimal usedCreditLimit) {
}
