package com.sahabatquran.webapp.repository;

import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.userCredential " +
           "LEFT JOIN FETCH u.userRoles ur " +
           "LEFT JOIN FETCH ur.role r " +
           "LEFT JOIN FETCH r.rolePermissions rp " +
           "LEFT JOIN FETCH rp.permission " +
           "WHERE u.username = :username AND u.isActive = true")
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE r.code = :roleCode AND u.isActive = true")
    List<User> findByRole_Code(@Param("roleCode") String roleCode);
    
    @Query("SELECT u FROM User u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE r.name = :roleName AND u.isActive = true")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u " +
           "JOIN u.userRoles ur " +
           "JOIN ur.role r " +
           "WHERE r.code = 'STUDENT' AND u.isActive = true " +
           "ORDER BY u.fullName ASC")
    List<User> findStudents();

    @Query("SELECT u FROM User u " +
           "JOIN Enrollment e ON e.student = u " +
           "WHERE e.classGroup = :classGroup AND e.status = 'ACTIVE' " +
           "ORDER BY u.fullName ASC")
    List<User> findStudentsByClassGroup(@Param("classGroup") ClassGroup classGroup);
}