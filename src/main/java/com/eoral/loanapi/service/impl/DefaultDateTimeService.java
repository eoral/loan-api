package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.service.DateTimeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DefaultDateTimeService implements DateTimeService {

    @Override
    public LocalDate getCurrentDateForCurrentBankBranch() {
        // I am using LocalDate.now() for simplicity. Normally, we should consider current bank branch time zone.
        return LocalDate.now();
    }

    @Override
    public LocalDate findMaxDueDateForPayableInstallments(LocalDate currentDate) {
        return currentDate.withDayOfMonth(1).plusMonths(3).minusDays(1);
    }
}
