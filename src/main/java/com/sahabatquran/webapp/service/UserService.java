package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    List<User> getTeachers();
}