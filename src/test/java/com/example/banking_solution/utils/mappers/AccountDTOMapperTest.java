package com.example.banking_solution.utils.mappers;

import com.example.banking_solution.dto.AccountDTO;
import com.example.banking_solution.models.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountDTOMapperTest {

    AccountDTOMapper accountDTOMapper;
    Account account;

    @BeforeEach
    void setup() {
        accountDTOMapper = new AccountDTOMapper();

        Account newAccount = new Account();
        newAccount.setAccountNumber("123456789");
        newAccount.setBalance(BigDecimal.valueOf(1000.50));

        account = newAccount;
    }

    @Test
    void apply_shouldMapAccountToAccountDTO() {
        AccountDTO accountDTO = accountDTOMapper.apply(account);

        assertNotNull(accountDTO);
        assertEquals(account.getAccountNumber(), accountDTO.accountNumber());
        assertEquals(account.getBalance().toString(), accountDTO.balance());
    }
}
