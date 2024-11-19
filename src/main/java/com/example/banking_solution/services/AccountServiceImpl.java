package com.example.banking_solution.services;

import com.example.banking_solution.dto.AccountRequestDTO;
import com.example.banking_solution.models.Account;
import com.example.banking_solution.repositories.AccountRepository;
import com.example.banking_solution.utils.AccountNumberGenerator;
import com.example.banking_solution.utils.exceptions.AccountNotFoundException;
import com.example.banking_solution.utils.exceptions.InsufficientFundsException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {


    AccountRepository accountRepository;
    AccountNumberGenerator accountNumberGenerator;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Override
    //@Transactional
    public Account createAccount(AccountRequestDTO accountRequestDTO) {

        Account account = new Account();

        account.setId(UUID.randomUUID().toString());

        String accountNumber;

        do {
            accountNumber = accountNumberGenerator.generateAccountNumber();
        } while (accountRepository.existsAccountByAccountNumber(accountNumber));

        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.valueOf(0.0));

        account =  accountRepository.save(account);

        log.info("Account with id [%s] created".formatted(account.getId()));

        return account;
    }

    @Override
    //@Transactional(readOnly = true)
    public Account findByAccountNumber(String accountNumber) {

        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("Account number must not be null or empty");
        }

        if (!accountNumber.matches("\\d{9}")) {
            throw new IllegalArgumentException("Account number has an invalid format");
        }

        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account with number [%s] not found".formatted(accountNumber)));
    }

    @Override
    //@Transactional(readOnly = true)
    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    @Override
   // @Transactional
    public Account depositFundsIntoAnAccount(String accountNumber, BigDecimal depositAmount) {

        if (depositAmount.compareTo(BigDecimal.ZERO) <= 0 || depositAmount.scale() > 2 ) {
            throw new IllegalArgumentException("Invalid value for deposit funds");
        }

        Account account = findByAccountNumber(accountNumber);

        BigDecimal updatedBalance = account.getBalance().add(depositAmount);
        account.setBalance(updatedBalance);

        log.info("Deposit of %s to account [%s] at %s".formatted(depositAmount, accountNumber, LocalDateTime.now()));

        return accountRepository.save(account);
    }

    @Override
    //@Transactional
    public Account withdrawFundsFromAnAccount(String accountNumber, BigDecimal withdrawAmount) {

        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0 || withdrawAmount.scale() > 2 ) {
            throw new IllegalArgumentException("Invalid value for withdraw funds");
        }

        Account account = findByAccountNumber(accountNumber);

        if (account.getBalance().compareTo(withdrawAmount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        BigDecimal updatedBalance = account.getBalance().subtract(withdrawAmount);
        account.setBalance(updatedBalance);

        log.info("Withdrawal of {} from account {} at {}", withdrawAmount, accountNumber, LocalDateTime.now());

        return accountRepository.save(account);
    }

    @Override
    //@Transactional
    public void transferFundsBetweenTwoAccount(String senderAccountNumber, String receiverAccountNumber, BigDecimal transferAmount) {

        if (senderAccountNumber.equals(receiverAccountNumber)) {
            throw new IllegalArgumentException("Sender and receiver accounts must be different");
        }

        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0 || transferAmount.scale() > 2 ) {
            throw new IllegalArgumentException("Invalid value for deposit funds");
        }

        Account senderAccount = findByAccountNumber(senderAccountNumber);

        if (senderAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }

        Account receiverAccount = findByAccountNumber(receiverAccountNumber);

        BigDecimal updateBalanceForSender = senderAccount.getBalance().subtract(transferAmount);
        senderAccount.setBalance(updateBalanceForSender);

        BigDecimal updateBalanceForReceiver = receiverAccount.getBalance().add(transferAmount);
        receiverAccount.setBalance(updateBalanceForReceiver);

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);

        log.info("Transfer of {} from account {} to account {} completed", transferAmount, senderAccountNumber, receiverAccountNumber);
    }

}
