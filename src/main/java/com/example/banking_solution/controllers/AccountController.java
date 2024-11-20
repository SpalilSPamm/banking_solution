package com.example.banking_solution.controllers;


import com.example.banking_solution.dto.AccountRequestDTO;
import com.example.banking_solution.models.Account;
import com.example.banking_solution.services.AccountService;
import com.example.banking_solution.utils.mappers.AccountDTOMapper;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;


@RestController
@RequestMapping("/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {


    AccountService accountService;
    AccountDTOMapper accountDTOMapper;

    @Autowired
    public AccountController(AccountService accountService, AccountDTOMapper accountDTOMapper) {
        this.accountService = accountService;
        this.accountDTOMapper = accountDTOMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {

        Account account = accountService.createAccount(accountRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountDTOMapper.apply(account));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) {

        Account account = accountService.findByAccountNumber(accountNumber);

        return ResponseEntity.ok(accountDTOMapper.apply(account));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAll());
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> depositFundsIntoAccount(@RequestParam String accountNumber,
                                                     @RequestParam BigDecimal depositAmount) {

        Account account = accountService.depositFundsIntoAnAccount(accountNumber, depositAmount);

        return ResponseEntity.ok(accountDTOMapper.apply(account));
    }


    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawFundsIntoAccount(@RequestParam String accountNumber,
                                                      @RequestParam BigDecimal withdrawAmount) {

        Account account = accountService.withdrawFundsFromAnAccount(accountNumber, withdrawAmount);
        return ResponseEntity.ok(accountDTOMapper.apply(account));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFundsBetweenAccounts(@RequestParam String senderAccountNumber,
                                                          @RequestParam String receiverAccountNumber,
                                                          @RequestParam BigDecimal transferAmount) {

        accountService.transferFundsBetweenTwoAccount(senderAccountNumber, receiverAccountNumber, transferAmount);

        return ResponseEntity.ok().build();
    }
}

