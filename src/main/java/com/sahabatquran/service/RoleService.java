package com.sahabatquran.service;

import com.sahabatquran.domain.Role;
import com.sahabatquran.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(UUID id) {
        return roleRepository.findById(id).orElse(null);
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public void deleteById(UUID id) {
        roleRepository.deleteById(id);
    }
}
