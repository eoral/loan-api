package com.eoral.loanapi.dto;

public record GetLoansOfCustomerRequest(Long customerId, Integer numberOfInstallments, Boolean isPaid) {
}
