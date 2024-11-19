package com.example.banking_solution.utils;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

public class AccountNumberGeneratorTest {

    @Test
    void generateAccountNumber_shouldReturn9DigitNumber() {

        Random mockRandom = mock(Random.class);

        when(mockRandom.nextInt(999999999)).thenReturn(123456789);

        AccountNumberGenerator accountNumberGenerator = new AccountNumberGenerator();
        accountNumberGenerator.random = mockRandom;

        String accountNumber = accountNumberGenerator.generateAccountNumber();

        assertNotNull(accountNumber, "Account number should not be null");
        assertEquals(9, accountNumber.length(), "Account number should have 9 digits");
        assertTrue(accountNumber.matches("\\d{9}"), "Account number should be a 9-digit number");
    }
}
