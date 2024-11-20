package com.example.banking_solution.dto;

import com.example.banking_solution.models.Account;

public record AccountDTO (String id,
                          String email,
                          String accountNumber,
                          String balance,
                          String role
                          ) {
    public AccountDTO(Account account) {
        this(account.getId(), account.getEmail() ,account.getAccountNumber(),
                account.getBalance().toString(), account.getRole().name());
    }
}
