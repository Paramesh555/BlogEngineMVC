package org.example.blogenginemvc.repos;

import org.example.blogenginemvc.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account, Long>{
    Optional<Account> findOneByEmail(String email);
    List<Account> findByEmail(String email);
}
