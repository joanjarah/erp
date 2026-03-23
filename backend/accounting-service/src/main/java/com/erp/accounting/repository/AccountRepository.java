package com.erp.accounting.repository;

import com.erp.accounting.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByStatus(Account.AccountStatus status);

    List<Account> findByAccountType(Account.AccountType accountType);

    @Query("SELECT a FROM Account a WHERE a.status = :status AND a.accountType = :accountType")
    List<Account> findByStatusAndAccountType(
        @Param("status") Account.AccountStatus status,
        @Param("accountType") Account.AccountType accountType
    );

    boolean existsByAccountNumber(String accountNumber);
}
