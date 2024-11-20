package com.example.banking_solution.models;

import com.example.banking_solution.utils.enums.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
@Table(name = "bank_accounts")
public class Account {

    @Id
    @UuidGenerator
    private String id;
    @Column(name = "account_number")
    private String accountNumber;
    private String email;
    private String password;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private RoleType role;


}
