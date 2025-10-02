package com.klaus.backend.Repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klaus.backend.Model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByName(String name);
    Boolean existsByName(String name);
}