package com.eoral.loanapi;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

    public static void assertBigDecimalsAreEqual(BigDecimal expected, BigDecimal actual) {
        assertEquals(0, expected.compareTo(actual));
    }
}
