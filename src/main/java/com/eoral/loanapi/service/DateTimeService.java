package com.eoral.loanapi.service;

import java.time.LocalDate;

public interface DateTimeService {

    LocalDate getCurrentDateForCurrentBankBranch();

    LocalDate findMaxDueDateForPayableInstallments(LocalDate currentDate);

    LocalDate calculateLoanInstallmentDueDate(LocalDate loanDate, int installmentNo);

}
