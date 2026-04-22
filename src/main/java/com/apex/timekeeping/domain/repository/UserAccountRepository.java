package com.apex.timekeeping.domain.repository;

import com.apex.timekeeping.domain.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    @Query("SELECT ua FROM UserAccount ua JOIN FETCH ua.employee e JOIN FETCH e.role WHERE ua.username = :username")
    Optional<UserAccount> findByUsernameWithEmployee(@Param("username") String username);

    boolean existsByUsername(String username);
}
