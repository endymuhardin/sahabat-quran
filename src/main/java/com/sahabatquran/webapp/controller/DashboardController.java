package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.entity.Role;
import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final UserRepository userRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get user from database using username from UserDetails
        Optional<User> userOptional = userRepository.findByUsernameWithRolesAndPermissions(userDetails.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("authorities", userDetails.getAuthorities());
            
            // Add role information for template visibility logic
            model.addAttribute("userRoles", user.getUserRoles().stream()
                .map(ur -> ur.getRole().getCode())
                .toList());
                
            log.info("User {} accessing unified dashboard with roles: {}", 
                user.getUsername(), 
                user.getUserRoles().stream()
                    .map(ur -> ur.getRole().getCode())
                    .toList());
            
        } else {
            // Fallback if user not found in database
            model.addAttribute("fullName", userDetails.getUsername());
            model.addAttribute("authorities", userDetails.getAuthorities());
            model.addAttribute("userRoles", new java.util.ArrayList<>());
            log.warn("User {} not found in database, using fallback", userDetails.getUsername());
        }
        
        return "dashboard";
    }
}