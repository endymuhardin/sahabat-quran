package com.sahabatquran.webapp.controller;

import com.sahabatquran.webapp.entity.User;
import com.sahabatquran.webapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
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
        } else {
            // Fallback if user not found in database
            model.addAttribute("fullName", userDetails.getUsername());
            model.addAttribute("authorities", userDetails.getAuthorities());
        }
        
        return "dashboard";
    }
}