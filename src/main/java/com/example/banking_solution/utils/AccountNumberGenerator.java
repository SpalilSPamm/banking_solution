package com.example.banking_solution.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AccountNumberGenerator {

    @Autowired
    Random random;

    public String generateAccountNumber() {
        int number = random.nextInt(999999999) + 1;
        return String.format("%09d", number);
    }
}
