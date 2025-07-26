package com.eoral.loanapi.util;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public class Constants {

    public static final Set<Integer> ALLOWED_NUMBER_OF_INSTALLMENTS = Set.of(6, 9, 12, 24);
    public static final String ALLOWED_NUMBER_OF_INSTALLMENTS_STR = String.join(
            ", ", ALLOWED_NUMBER_OF_INSTALLMENTS.stream().sorted().map(String::valueOf).collect(Collectors.toList()));

    public static final BigDecimal MIN_INTEREST_RATE = new BigDecimal("0.1");
    public static final BigDecimal MAX_INTEREST_RATE = new BigDecimal("0.5");
    public static final String INTEREST_RATE_RANGE_STR = MIN_INTEREST_RATE.toString() + " - " + MAX_INTEREST_RATE;

    public static final int EARLY_PAYMENT_MAX_MONTHS = 3;

    public static final String ADMIN_USER = "admin";
    public static final String NON_ADMIN_USER = "user";

}
