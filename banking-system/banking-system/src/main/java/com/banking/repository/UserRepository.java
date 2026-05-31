package com.banking.repository;

import com.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — Spring Data JPA automatically implements this interface.
 *
 * How it works:
 *   1. Extend JpaRepository<Entity, PrimaryKeyType>
 *   2. Spring generates the SQL for you at startup — no boilerplate DAO needed.
 *   3. Method names like findByEmail() are parsed and converted to:
 *      SELECT * FROM users WHERE email = ?
 *
 * Inherited methods (for free): save(), findById(), findAll(),
 *   deleteById(), count(), existsById(), etc.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
