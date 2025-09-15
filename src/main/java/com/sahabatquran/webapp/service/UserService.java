package com.sahabatquran.webapp.service;

import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    public List<User> getTeachers() {
        log.debug("Finding all teachers");
        return userRepository.findByRole_Code("INSTRUCTOR");
    }
}