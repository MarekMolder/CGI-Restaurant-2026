package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link com.example.CGI_Restaurant.domain.entities.User}. Used for login and default admin setup.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /** Finds a user by email (unique). */
    Optional<User> findByEmail(String email);

}
