package com.codewithmosh.arypto.repositories;

import com.codewithmosh.arypto.entities.User;
import com.codewithmosh.arypto.entities.Wallet;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = "cryptoWallets.user")
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> getUserWithWallets(String userId);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
