package com.example.banking_solution.services;

import com.example.banking_solution.dto.AccountRequestDTO;
import com.example.banking_solution.models.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

     Account createAccount(AccountRequestDTO accountRequestDTO);
     Account findByAccountNumber(String accountNumber);
     List<Account> getAll();

     Account depositFundsIntoAnAccount(String accountNumber, BigDecimal depositAmount);
     Account withdrawFundsFromAnAccount(String accountNumber, BigDecimal withdrawAmount);
     void transferFundsBetweenTwoAccount(String senderAccountNumber, String receiverAccountNumber, BigDecimal transferAmount);
}
