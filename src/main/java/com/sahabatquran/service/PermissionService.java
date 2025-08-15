package com.sahabatquran.service;

import com.sahabatquran.domain.Permission;
import com.sahabatquran.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public Permission findById(UUID id) {
        return permissionRepository.findById(id).orElse(null);
    }

    public void save(Permission permission) {
        permissionRepository.save(permission);
    }

    public void deleteById(UUID id) {
        permissionRepository.deleteById(id);
    }
}
