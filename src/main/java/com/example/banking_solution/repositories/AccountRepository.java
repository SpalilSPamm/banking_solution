package com.example.banking_solution.repositories;

import com.example.banking_solution.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsAccountByAccountNumber(String accountNumber);

}
