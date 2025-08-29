package com.sahabatquran.webapp.service.impl;

import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;
import com.sahabatquran.webapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }
    
    @Override
    public Optional<User> findById(UUID id) {
        log.debug("Finding user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    @Override
    public List<User> getTeachers() {
        log.debug("Finding all teachers");
        return userRepository.findByRole_Code("INSTRUCTOR");
    }
}