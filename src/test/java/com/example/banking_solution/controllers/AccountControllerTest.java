package com.example.banking_solution.controllers;

import com.example.banking_solution.TestUtil;
import com.example.banking_solution.dto.AccountDTO;
import com.example.banking_solution.dto.AccountRequestDTO;
import com.example.banking_solution.models.Account;
import com.example.banking_solution.services.AccountService;
import com.example.banking_solution.utils.enums.RoleType;
import com.example.banking_solution.utils.exceptions.AccountNotFoundException;
import com.example.banking_solution.utils.exceptions.InsufficientFundsException;
import com.example.banking_solution.utils.exceptions.PasswordDontMatchException;
import com.example.banking_solution.utils.mappers.AccountDTOMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService accountService;
    @MockBean
    AccountDTOMapper accountDTOMapper;

    List<Account> accounts;

    @BeforeEach
    void beforeEach() {

        Account firstAccount = new Account();

        firstAccount.setId(UUID.randomUUID().toString());
        firstAccount.setBalance(BigDecimal.valueOf(0));
        firstAccount.setEmail("email@gmail.com");
        firstAccount.setPassword("password");
        firstAccount.setAccountNumber("000000001");
        firstAccount.setRole(RoleType.USER);

        Account secondAccount = new Account();

        secondAccount.setId(UUID.randomUUID().toString());
        secondAccount.setBalance(BigDecimal.valueOf(100.00));
        secondAccount.setEmail("email@gmail.com");
        secondAccount.setPassword("password");
        secondAccount.setAccountNumber("000000002");
        secondAccount.setRole(RoleType.USER);

        Account thirdAccount = new Account();

        thirdAccount.setId(UUID.randomUUID().toString());
        thirdAccount.setBalance(BigDecimal.valueOf(1000.00));
        thirdAccount.setEmail("email@gmail.com");
        thirdAccount.setPassword("password");
        thirdAccount.setAccountNumber("000000003");
        thirdAccount.setRole(RoleType.USER);

        accounts = List.of(firstAccount, secondAccount, thirdAccount);
    }

    @Test
    void createAccount_returnCreate() throws Exception {

        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("email@email.com",
                 "password", "password");

        Account account = accounts.get(0);

        AccountDTO accountDTO = new AccountDTO(account);

        when(accountService.createAccount(accountRequestDTO)).thenReturn(account);
        when(accountDTOMapper.apply(account)).thenReturn(accountDTO);

        this.mockMvc.perform(
                    post("/accounts/create")
                    .contentType(TestUtil.APPLICATION_JSON_UTF8)
                    .content(TestUtil.convertObjectToJsonBytes(accountRequestDTO))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(account.getId())))
                .andExpect(jsonPath("$.accountNumber", equalTo("000000001")))
                .andExpect(jsonPath("$.balance", equalTo("0")));
    }

    @Test

    void createAccount_returnBadRequest_whenPasswordAndConfirmPasswordDontMatch() throws Exception {

        AccountRequestDTO accountRequestDTO = new AccountRequestDTO("email@email.com",
                "password", "differentPassword");

        when(accountService.createAccount(accountRequestDTO))
                .thenThrow(new PasswordDontMatchException("Password and confirm password don't match"));

        this.mockMvc.perform(
                        post("/accounts/create")
                                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                                .content(TestUtil.convertObjectToJsonBytes(accountRequestDTO))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Password and confirm password don't match"));
    }


    @Test
    void getAccountByAccountNumber_returnOk() throws Exception {

        Account account = accounts.get(1);

        String accountNumber = account.getAccountNumber();

        AccountDTO accountDTO = new AccountDTO(account);

        when(accountService.findByAccountNumber(accountNumber)).thenReturn(account);
        when(accountDTOMapper.apply(account)).thenReturn(accountDTO);

        this.mockMvc.perform(get("/accounts/%s".formatted(account.getAccountNumber())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(account.getId())))
                .andExpect(jsonPath("$.accountNumber", equalTo(account.getAccountNumber())))
                .andExpect(jsonPath("$.balance", equalTo(account.getBalance().toString())));

    }

    @Test
    void getAccountByAccountNumber_returnNotFoundAccount() throws Exception {

        String accountNumber = "000000001";

        when(accountService.findByAccountNumber(accountNumber))
                .thenThrow(new AccountNotFoundException("Account with number [%s] not found".formatted(accountNumber)));

        this.mockMvc.perform(get("/accounts/%s".formatted(accountNumber)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Account with number [%s] not found".formatted(accountNumber)));

    }

    @Test
    void getAccountByAccountNumber_returnNotFound_whenAccountNumberIsBlanc() throws Exception {

        String accountNumber = "    ";

        when(accountService.findByAccountNumber(accountNumber))
                .thenThrow(new IllegalArgumentException("Account number must not be null or empty"));

        this.mockMvc.perform(get("/accounts/%s".formatted(accountNumber)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Account number must not be null or empty"));

    }

    @Test
    void getAccountByAccountNumber_returnNotFound_whenAccountNumberHasAnInvalidFormat() throws Exception {

        String accountNumber = "    ";

        when(accountService.findByAccountNumber(accountNumber))
                .thenThrow(new IllegalArgumentException("Account number has an invalid format"));

        this.mockMvc.perform(get("/accounts/%s".formatted(accountNumber)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Account number has an invalid format"));

    }

    @Test
    void getAll_returnOk() throws Exception {

        when(accountService.getAll()).thenReturn(accounts);

        this.mockMvc.perform(get("/accounts/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(accounts.get(0).getId())))
                .andExpect(jsonPath("$.[1].id", equalTo(accounts.get(1).getId())))
                .andExpect(jsonPath("$.[2].id", equalTo(accounts.get(2).getId())));
    }

    @Test
    void depositFundsIntoAccount_returnOk() throws Exception {

        Account account = accounts.get(1);
        BigDecimal depositAmount = BigDecimal.valueOf(100.00);

        AccountDTO accountDTO = new AccountDTO(account);

        when(accountService.depositFundsIntoAnAccount(account.getAccountNumber(), depositAmount)).thenReturn(account);
        when(accountDTOMapper.apply(account)).thenReturn(accountDTO);

        this.mockMvc.perform(
                post("/accounts/deposit")
                        .param("accountNumber", account.getAccountNumber())
                        .param("depositAmount", depositAmount.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
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
    void depositFundsIntoAccount_returnBadRequest_whenDepositAmountIsInvalid(BigDecimal invalidAmount) throws Exception {

        String accountNumber = "000000001";

        when(accountService.depositFundsIntoAnAccount(accountNumber, invalidAmount))
                .thenThrow(new IllegalArgumentException("Invalid value for deposit funds"));

        this.mockMvc.perform(
                        post("/accounts/deposit")
                                .param("accountNumber", accountNumber)
                                .param("depositAmount", String.valueOf(invalidAmount))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid value for deposit funds"));

    }

    @Test
    void withdrawFundsIntoAccount_returnOk() throws Exception {

        Account account = accounts.get(1);
        BigDecimal withdrawAmount = BigDecimal.valueOf(100.00);

        AccountDTO accountDTO = new AccountDTO(account);

        when(accountService.withdrawFundsFromAnAccount(account.getAccountNumber(), withdrawAmount)).thenReturn(account);
        when(accountDTOMapper.apply(account)).thenReturn(accountDTO);

        this.mockMvc.perform(
                        post("/accounts/withdraw")
                                .param("accountNumber", account.getAccountNumber())
                                .param("withdrawAmount", withdrawAmount.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(account.getId()))
                .andExpect(jsonPath("$.accountNumber").value(account.getAccountNumber()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
    }

    @Test
    void withdrawFundsIntoAccount_returnBadRequest_whenInsufficientFunds() throws Exception {

        String accountNumber = "000000001";

        BigDecimal withdrawAmount = BigDecimal.valueOf(10000.00);

        when(accountService.withdrawFundsFromAnAccount(accountNumber, withdrawAmount))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));

        this.mockMvc.perform(
                    post("/accounts/withdraw")
                            .param("accountNumber", accountNumber)
                            .param("withdrawAmount", String.valueOf(withdrawAmount))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountData")
    void withdrawFundsIntoAccount_returnBadRequest_whenWithdrawAmountIsInvalid(BigDecimal invalidAmount) throws Exception {

        String accountNumber = "000000001";

        when(accountService.withdrawFundsFromAnAccount(accountNumber, invalidAmount))
                .thenThrow(new IllegalArgumentException("Invalid value for withdraw funds"));

        this.mockMvc.perform(
                        post("/accounts/withdraw")
                                .param("accountNumber", accountNumber)
                                .param("withdrawAmount", String.valueOf(invalidAmount))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid value for withdraw funds"));
    }

    @Test
    void transferFundsBetweenAccounts_returnOk() throws Exception {

        String senderAccountNumber = "000000001";

        String receiverAccountNumber = "000000002";

        BigDecimal transferAmount = BigDecimal.valueOf(200.00);

        this.mockMvc.perform(
                        post("/accounts/transfer")
                                .param("senderAccountNumber", senderAccountNumber)
                                .param("receiverAccountNumber", receiverAccountNumber)
                                .param("transferAmount", transferAmount.toString())
                )
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAmountData")
    void transferFundsBetweenAccounts_returnBadRequest_whenTransferAmountIsInvalid(BigDecimal invalidAmount) throws Exception {

        String senderAccountNumber = "000000001";

        String receiverAccountNumber = "000000002";

        doThrow(new IllegalArgumentException("Invalid value for transfer funds"))
                .when(accountService)
                .transferFundsBetweenTwoAccount(senderAccountNumber, receiverAccountNumber, invalidAmount);

        this.mockMvc.perform(
                        post("/accounts/transfer")
                                .param("senderAccountNumber", senderAccountNumber)
                                .param("receiverAccountNumber", receiverAccountNumber)
                                .param("transferAmount", invalidAmount.toString())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid value for transfer funds"));
    }

    @Test
    void transferFundsBetweenAccounts_returnBadRequest_whenSenderAccountNumberEqualsReceiverAccountNumber() throws Exception {

        String accountNumber = "000000001";

        BigDecimal transferAmount = BigDecimal.valueOf(200.00);

        doThrow(new IllegalArgumentException("Sender and receiver accounts must be different"))
                .when(accountService)
                .transferFundsBetweenTwoAccount(accountNumber, accountNumber, transferAmount);

        this.mockMvc.perform(
                        post("/accounts/transfer")
                                .param("senderAccountNumber", accountNumber)
                                .param("receiverAccountNumber", accountNumber)
                                .param("transferAmount", transferAmount.toString())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Sender and receiver accounts must be different"));
    }

    @Test
    void transferFundsBetweenAccounts_returnBadRequest_whenInsufficientFunds() throws Exception {

        String senderAccountNumber = "000000001";

        String receiverAccountNumber = "000000002";

        BigDecimal transferAmount = BigDecimal.valueOf(20000.00);

        doThrow(new InsufficientFundsException("Insufficient funds for transfer"))
                .when(accountService)
                .transferFundsBetweenTwoAccount(senderAccountNumber, receiverAccountNumber, transferAmount);

        this.mockMvc.perform(
                        post("/accounts/transfer")
                                .param("senderAccountNumber", senderAccountNumber)
                                .param("receiverAccountNumber", receiverAccountNumber)
                                .param("transferAmount", transferAmount.toString())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Insufficient funds for transfer"));
    }
}
