package com.eoral.loanapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DefaultDateTimeServiceTest {

    @InjectMocks
    private DefaultDateTimeService dateTimeService;

    @Test
    public void findMaxDueDateForPayableInstallmentsShouldAddThreeCalendarMonths() {
        LocalDate currentDate = LocalDate.of(2025, 1, 1);
        LocalDate maxDueDate = dateTimeService.findMaxDueDateForPayableInstallments(currentDate);
        assertEquals("2025-03-31", maxDueDate.toString());
    }

    @Test
    public void calculateLoanInstallmentDueDateShouldReturnFirstDayOfNextMonthForFirstInstallment() {
        for (int day = 1; day <= 31; day++) {
            LocalDate loanDate = LocalDate.of(2025, 1, day);
            assertEquals("2025-02-01",
                    dateTimeService.calculateLoanInstallmentDueDate(loanDate, 1).toString());
        }
    }

    @Test
    public void calculateLoanInstallmentDueDateShouldIncrementMonthInResultAccordingToInstallmentNo() {
        LocalDate loanDate = LocalDate.of(2025, 1, 1);
        assertEquals("2025-02-01",
                dateTimeService.calculateLoanInstallmentDueDate(loanDate, 1).toString());
        assertEquals("2025-03-01",
                dateTimeService.calculateLoanInstallmentDueDate(loanDate, 2).toString());
        assertEquals("2025-04-01",
                dateTimeService.calculateLoanInstallmentDueDate(loanDate, 3).toString());
    }
}
