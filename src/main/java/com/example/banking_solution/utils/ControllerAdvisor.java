package com.example.banking_solution.utils;

import com.example.banking_solution.utils.exceptions.AccountNotFoundException;
import com.example.banking_solution.utils.exceptions.InsufficientFundsException;
import com.example.banking_solution.utils.exceptions.PasswordDontMatchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvisor {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Object> handleAccountNotFoundException(
            AccountNotFoundException ex
    ) {

        Map<String, Object> body = new HashMap<>();

        body.put("status", "error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex
    ) {

        Map<String, Object> body = new HashMap<>();

        body.put("status", "error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleInsufficientFundsException(
            InsufficientFundsException ex
    ) {
        Map<String, Object> body = new HashMap<>();

        body.put("status", "error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordDontMatchException.class)
    public ResponseEntity<Object> handlePasswordDontMatchException(
            PasswordDontMatchException ex
    ) {
        Map<String, Object> body = new HashMap<>();

        body.put("status", "error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
