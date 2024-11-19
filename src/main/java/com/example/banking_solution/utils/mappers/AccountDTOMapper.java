package com.example.banking_solution.utils.mappers;

import com.example.banking_solution.dto.AccountDTO;
import com.example.banking_solution.models.Account;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AccountDTOMapper implements Function<Account, AccountDTO> {

    @Override
    public AccountDTO apply(Account account) {
        return new AccountDTO(account);
    }
}
