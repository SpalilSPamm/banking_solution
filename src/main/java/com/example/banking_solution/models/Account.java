package com.example.banking_solution.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Account {

    private String id;
    private String accountNumber;
//    private String nickname;
//    private String password;
    private BigDecimal balance;
    //private Role role;


}
