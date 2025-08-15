package com.sahabatquran.repository;

import com.sahabatquran.domain.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPasswordRepository extends JpaRepository<UserPassword, UUID> {
}
