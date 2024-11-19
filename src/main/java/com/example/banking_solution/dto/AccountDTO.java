package com.example.banking_solution.dto;

import com.example.banking_solution.models.Account;

public record AccountDTO (String id,
                          String accountNumber,
                          String balance) {
    public AccountDTO(Account account) {
        this(account.getId(), account.getAccountNumber(), account.getBalance().toString());
    }
}
