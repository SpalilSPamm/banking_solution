package com.example.banking_solution.services;

import com.example.banking_solution.dto.AccountRequestDTO;
import com.example.banking_solution.models.Account;
import com.example.banking_solution.repositories.AccountRepository;
import com.example.banking_solution.utils.AccountNumberGenerator;
import com.example.banking_solution.utils.exceptions.AccountNotFoundException;
import com.example.banking_solution.utils.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class AccountServiceTest {

    @Spy
    @InjectMocks
    AccountServiceImpl accountServiceImpl;

    @Mock
    AccountRepository accountRepository;
    @Mock
    AccountNumberGenerator accountNumberGenerator;

    List<Account> accounts;

    @BeforeEach
    void beforeEach() {

        Account firstAccount = new Account();

        firstAccount.setId(UUID.randomUUID().toString());
        firstAccount.setBalance(BigDecimal.valueOf(0));
        firstAccount.setAccountNumber("000000001");

        Account secondAccount = new Account();

        secondAccount.setId(UUID.randomUUID().toString());
        secondAccount.setBalance(BigDecimal.valueOf(100.00));
        secondAccount.setAccountNumber("000000002");

        Account thirdAccount = new Account();

        thirdAccount.setId(UUID.randomUUID().toString());
        thirdAccount.setBalance(BigDecimal.valueOf(1000.00));
        thirdAccount.setAccountNumber("000000003");


        accounts = List.of(firstAccount, secondAccount, thirdAccount);
    }

    @Test
    void createAccount_successfully_whereGeneratedAccountNumberIsUnique() {

        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();

        var newAccount = accounts.get(0);

        when(accountRepository.existsAccountByAccountNumber(any()))
                .thenReturn(false);
        when(accountNumberGenerator.generateAccountNumber()).thenReturn("000000001");
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        var result  = accountServiceImpl.createAccount(accountRequestDTO);

        assertNotNull(newAccount.getId());
        assertEquals(result.getAccountNumber(), newAccount.getAccountNumber());

        verify(accountNumberGenerator, Mockito.times(1)).generateAccountNumber();

    }

    @Test
    void createAccount_successfully_whereGeneratedAccountNumberIsNotUniqueTheFirstTime() {

        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();

        var newAccount  = accounts.get(0);

        when(accountNumberGenerator.generateAccountNumber()).thenReturn("000000001");
        when(accountRepository.existsAccountByAccountNumber(any()))
                .thenReturn(true)
                .thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        var result  = accountServiceImpl.createAccount(accountRequestDTO);

        assertNotNull(newAccount.getId());
        assertEquals(newAccount.getAccountNumber(), result.getAccountNumber());
        assertEquals(BigDecimal.valueOf(0), newAccount.getBalance());
        verify(accountNumberGenerator, Mockito.times(2)).generateAccountNumber();

        verify(accountRepository, Mockito.times(2)).existsAccountByAccountNumber(any());
    }

    @Test
    void findByAccountNumber_successfully() {

        String accountNumber = "000000002";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(accounts.get(1)));

        var result = accountServiceImpl.findByAccountNumber(accountNumber);

        assertNotNull(result);
        assertEquals(accountNumber, result.getAccountNumber());

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);

    }

    @Test
    void findByAccountNumber_whenAccountNotFound() {

        String accountNumber = "000000001";

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> accountServiceImpl.findByAccountNumber(accountNumber));

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
    }

    static Stream<Arguments> provideAccountNumberInvalidData() {
        return Stream.of(
                Arguments.of((Object) null),
                Arguments.of(" "),
                Arguments.of("0001")
        );
    }

    @ParameterizedTest
    @MethodSource("provideAccountNumberInvalidData")
    void findByAccountNumber_whenInvalidFormatAccountNumber(String accountNumber) {

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.findByAccountNumber(accountNumber));

        verify(accountRepository, times(0)).findByAccountNumber(accountNumber);
    }

    @Test
    void findAll_successfully() {

        when(this.accountRepository.findAll()).thenReturn(accounts);

         var responseEntity = accountServiceImpl.getAll();

        assertNotNull(responseEntity);
        assertEquals(responseEntity.size(), accounts.size());
        assertEquals(responseEntity.get(0).getId(), accounts.get(0).getId());
        assertEquals(responseEntity.get(0).getBalance(), accounts.get(0).getBalance());
        assertEquals(responseEntity.get(0).getAccountNumber(), accounts.get(0).getAccountNumber());
        assertEquals(responseEntity.get(1).getBalance(), accounts.get(1).getBalance());
        assertEquals(responseEntity.get(1).getAccountNumber(), accounts.get(1).getAccountNumber());
        assertEquals(responseEntity.get(2).getBalance(),accounts.get(2).getBalance());
        assertEquals(responseEntity.get(2).getAccountNumber(), accounts.get(2).getAccountNumber());
    }

    static Stream<Arguments> provideAccountAndDepositData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(0), BigDecimal.valueOf(100.00), BigDecimal.valueOf(100.00)),
                Arguments.of(BigDecimal.valueOf(500.00), BigDecimal.valueOf(50.00), BigDecimal.valueOf(550.00)),
                Arguments.of(BigDecimal.valueOf(20.00), BigDecimal.valueOf(10.50), BigDecimal.valueOf(30.50))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAccountAndDepositData")
    void handleDepositFundsIntoAnAccount_successfully(BigDecimal initialBalance, BigDecimal depositAmount, BigDecimal expectedBalance) {

        var account = new Account();
        account.setAccountNumber("000000001");
        account.setBalance(initialBalance);

        when(accountRepository.findByAccountNumber("000000001"))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = accountServiceImpl.depositFundsIntoAnAccount("000000001", depositAmount);

        assertNotNull(result);
        assertEquals(expectedBalance, result.getBalance());
        verify(accountRepository, times(1)).findByAccountNumber("000000001");
        verify(accountRepository, times(1)).save(account);
    }

    static Stream<Arguments> provideInvalidAmountData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(-100.00)),
                Arguments.of(BigDecimal.valueOf(0)),
                Arguments.of(BigDecimal.valueOf(0.0005))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountData")
    void depositFundsIntoAnAccount_throwInvalidAmountValue(BigDecimal invalidAmountFunds) {

        var account = new Account();
        account.setAccountNumber("000000001");
        account.setBalance(BigDecimal.valueOf(100.00));

        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.depositFundsIntoAnAccount("000000001", invalidAmountFunds));

        verify(accountRepository, times(0)).findByAccountNumber("000000001");
        verify(accountRepository, times(0)).save(account);
    }

    static Stream<Arguments> provideAccountAndWithdrawData() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100.00), BigDecimal.valueOf(50.00), BigDecimal.valueOf(50.00)),
                Arguments.of(BigDecimal.valueOf(500.00), BigDecimal.valueOf(1), BigDecimal.valueOf(499.00)),
                Arguments.of(BigDecimal.valueOf(20.00), BigDecimal.valueOf(10.50), BigDecimal.valueOf(9.50))
        );
    }

    @ParameterizedTest
    @MethodSource("provideAccountAndWithdrawData")
    void withdrawFundsFromAnAccount_successfully(BigDecimal initialBalance, BigDecimal withdrawAmount, BigDecimal expectedBalance) {

        var account = new Account();
        account.setAccountNumber("000000001");
        account.setBalance(initialBalance);

        when(accountRepository.findByAccountNumber("000000001"))
                .thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = accountServiceImpl.withdrawFundsFromAnAccount(account.getAccountNumber(), withdrawAmount);

        assertNotNull(result);
        assertEquals(expectedBalance, result.getBalance());
        verify(accountRepository, times(1)).findByAccountNumber("000000001");
        verify(accountRepository, times(1)).save(account);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountData")
    void withdrawFundsFromAnAccount_throwInvalidAmountValue(BigDecimal invalidAmountFunds) {

        var account = new Account();
        account.setAccountNumber("000000001");
        account.setBalance(BigDecimal.valueOf(100.00));

        assertThrows(IllegalArgumentException.class, () -> accountServiceImpl.withdrawFundsFromAnAccount("000000001", invalidAmountFunds));

        verify(accountRepository, times(0)).findByAccountNumber("000000001");
        verify(accountRepository, times(0)).save(account);
    }

    @Test
    void withdrawFundsFromAnAccount_throwInsufficientFundsForWithdrawal() {

        var account = new Account();
        account.setAccountNumber("000000001");
        account.setBalance(BigDecimal.valueOf(100.00));

        BigDecimal withdrawAmount = BigDecimal.valueOf(200.00);

        when(accountRepository.findByAccountNumber("000000001"))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class,
                () -> accountServiceImpl.withdrawFundsFromAnAccount("000000001", withdrawAmount));

        verify(accountRepository, times(1)).findByAccountNumber("000000001");
        verify(accountRepository, times(0)).save(account);
    }

    @Test
    void transferFundsBetweenTwoAccount_successfully() {

        var senderAccount = new Account();

        senderAccount.setAccountNumber("000000001");
        senderAccount.setBalance(BigDecimal.valueOf(100.00));

        var receiverAccount = new Account();

        receiverAccount.setAccountNumber("000000002");
        receiverAccount.setBalance(BigDecimal.valueOf(50.00));

        BigDecimal transferAmount = BigDecimal.valueOf(55.55);

        when(accountRepository.findByAccountNumber(any()))
                .thenReturn(Optional.of(senderAccount))
                .thenReturn(Optional.of(receiverAccount));

        accountServiceImpl.transferFundsBetweenTwoAccount(senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), transferAmount);

        assertEquals(BigDecimal.valueOf(44.45), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(105.55), receiverAccount.getBalance());
        verify(accountRepository, times(2)).findByAccountNumber(any());
        verify(accountRepository, times(1)).save(senderAccount);
        verify(accountRepository, times(1)).save(receiverAccount);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountData")
    void transferFundsBetweenTwoAccount_throwInvalidAmountValue(BigDecimal invalidAmountFunds) {

        var senderAccount = new Account();

        senderAccount.setAccountNumber("000000001");
        senderAccount.setBalance(BigDecimal.valueOf(100.00));

        var receiverAccount = new Account();

        receiverAccount.setAccountNumber("000000002");
        receiverAccount.setBalance(BigDecimal.valueOf(50.00));

        assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.transferFundsBetweenTwoAccount(senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), invalidAmountFunds));


        assertEquals(BigDecimal.valueOf(100.00), senderAccount.getBalance());
        assertEquals(BigDecimal.valueOf(50.00), receiverAccount.getBalance());

        verify(accountRepository, times(0)).findByAccountNumber(any());
        verify(accountRepository, times(0)).save(senderAccount);
        verify(accountRepository, times(0)).save(receiverAccount);
    }

    @Test
    void transferFundsBetweenTwoAccount_throwInsufficientFundsForTransfer() {

        var senderAccount = new Account();

        senderAccount.setAccountNumber("000000001");
        senderAccount.setBalance(BigDecimal.valueOf(100.00));

        var receiverAccount = new Account();

        receiverAccount.setAccountNumber("000000002");
        receiverAccount.setBalance(BigDecimal.valueOf(50.00));

        BigDecimal transferAmount = BigDecimal.valueOf(300.00);

        when(accountRepository.findByAccountNumber(any()))
                .thenReturn(Optional.of(senderAccount));

        assertThrows(InsufficientFundsException.class,
                () -> accountServiceImpl.transferFundsBetweenTwoAccount(senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), transferAmount));

        verify(accountRepository, times(1)).findByAccountNumber(any());
        verify(accountRepository, times(0)).save(senderAccount);
        verify(accountRepository, times(0)).save(receiverAccount);
    }

    @Test
    void transferFundsBetweenTwoAccount_throwEqualAccountNumbers() {

        var senderAccount = new Account();

        senderAccount.setAccountNumber("000000001");
        senderAccount.setBalance(BigDecimal.valueOf(100.00));

        var receiverAccount = new Account();

        receiverAccount.setAccountNumber("000000001");
        receiverAccount.setBalance(BigDecimal.valueOf(50.00));

        BigDecimal transferAmount = BigDecimal.valueOf(100.00);

        assertThrows(IllegalArgumentException.class,
                () -> accountServiceImpl.transferFundsBetweenTwoAccount(senderAccount.getAccountNumber(), receiverAccount.getAccountNumber(), transferAmount));

        verify(accountRepository, times(0)).findByAccountNumber(any());
        verify(accountRepository, times(0)).save(senderAccount);
        verify(accountRepository, times(0)).save(receiverAccount);
    }
}