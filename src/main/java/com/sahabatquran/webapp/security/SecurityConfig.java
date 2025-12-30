package com.sahabatquran.webapp.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final DataSource dataSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/error", "/register/**", "/feedback/anonymous/**").permitAll()
                // Dashboard - accessible to all authenticated users
                .requestMatchers("/dashboard").authenticated()
                // User Management Module
                .requestMatchers("/users/**").hasAnyAuthority("USER_VIEW", "USER_CREATE", "USER_EDIT", "USER_DELETE", "USER_ACTIVATE")
                // Academic Management Module  
                .requestMatchers("/classes/**").hasAnyAuthority("CLASS_VIEW", "CLASS_CREATE", "CLASS_EDIT", "CLASS_DELETE", "CLASS_SCHEDULE_MANAGE")
                .requestMatchers("/enrollments/**").hasAnyAuthority("ENROLLMENT_VIEW", "ENROLLMENT_CREATE", "ENROLLMENT_EDIT", "ENROLLMENT_APPROVE", "ENROLLMENT_CANCEL")
                .requestMatchers("/attendance/**").hasAnyAuthority("ATTENDANCE_VIEW", "ATTENDANCE_MARK", "ATTENDANCE_EDIT", "ATTENDANCE_REPORT")
                .requestMatchers("/assessments/**").hasAnyAuthority("ASSESSMENT_VIEW", "ASSESSMENT_CREATE", "ASSESSMENT_EDIT", "ASSESSMENT_GRADE")
                .requestMatchers("/report-cards/**").hasAnyAuthority("REPORT_CARD_VIEW", "REPORT_CARD_GENERATE")
                // Finance Management Module
                .requestMatchers("/billing/**").hasAnyAuthority("BILLING_VIEW", "BILLING_CREATE", "BILLING_EDIT")
                .requestMatchers("/payments/**").hasAnyAuthority("PAYMENT_VIEW", "PAYMENT_VERIFY", "PAYMENT_RECORD")
                .requestMatchers("/payroll/**").hasAnyAuthority("SALARY_VIEW", "SALARY_CALCULATE", "SALARY_APPROVE")
                // Event Management Module
                .requestMatchers("/events/**").hasAnyAuthority("EVENT_VIEW", "EVENT_CREATE", "EVENT_EDIT", "EVENT_DELETE", "EVENT_REGISTER", "EVENT_MANAGE_REGISTRATION")
                // Student Registration Management Module
                .requestMatchers("/registrations/**").hasAnyAuthority("STUDENT_REG_VIEW", "STUDENT_REG_CREATE", "STUDENT_REG_EDIT", "STUDENT_REG_REVIEW", "STUDENT_REG_ASSIGN_TEACHER", "STUDENT_REG_REPORT", "PLACEMENT_TEST_EVALUATE")
                // Academic Planning Module
                .requestMatchers("/academic/**").hasAnyAuthority("ACADEMIC_TERM_MANAGE", "TEACHER_AVAILABILITY_VIEW", "CLASS_GENERATION_RUN", "CLASS_GENERATION_REVIEW", "SCHEDULE_APPROVE", "SYSTEM_GOLIVE_MANAGE", "TEACHER_LEVEL_ASSIGN", "TERM_CLOSURE_MANAGE", "TERM_CLOSURE_EXECUTE")
                // Analytics Module (more specific rule must come before general management rule)
                .requestMatchers("/management/analytics/**").hasAuthority("ANALYTICS_VIEW")
                // Management Module
                .requestMatchers("/management/**").hasAnyAuthority("TEACHER_LEVEL_ASSIGN", "REPORT_OPERATIONAL", "REPORT_FINANCIAL", "REPORT_ACADEMIC", "REPORT_EXPORT", "AUDIT_LOG_VIEW")
                // Instructor Module
                .requestMatchers("/instructor/**").hasAnyAuthority("TEACHER_AVAILABILITY_SUBMIT", "STUDENT_REG_VIEW", "STUDENT_REG_REVIEW", "STUDENT_REG_EVALUATE", "PLACEMENT_TEST_EVALUATE")
                // System & Reporting Module
                .requestMatchers("/reports/**").hasAnyAuthority("REPORT_OPERATIONAL", "REPORT_FINANCIAL", "REPORT_ACADEMIC", "REPORT_EXPORT", "DASHBOARD_VIEW")
                .requestMatchers("/system/**").hasAnyAuthority("SYSTEM_CONFIG", "BACKUP_RESTORE", "AUDIT_LOG_VIEW")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .sessionManagement(session -> session
                .sessionFixation().migrateSession()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/error/403")
            )
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline' https://unpkg.com https://cdn.jsdelivr.net; " +
                        "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net; " +
                        "font-src 'self' https://cdnjs.cloudflare.com; " +
                        "img-src 'self' data: https:; " +
                        "connect-src 'self';"
                    )
                )
            );

        return http.build();
    }
    
    @Bean
    public JdbcUserDetailsManager jdbcUserDetailsManager() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        
        // Custom queries for our database schema
        userDetailsManager.setUsersByUsernameQuery(
            "SELECT u.username, uc.password_hash, u.is_active " +
            "FROM users u " +
            "JOIN user_credentials uc ON u.id = uc.id_user " +
            "WHERE u.username = ?"
        );
        
        userDetailsManager.setAuthoritiesByUsernameQuery(
            "SELECT u.username, p.code as authority " +
            "FROM users u " +
            "JOIN user_roles ur ON u.id = ur.id_user " +
            "JOIN role_permissions rp ON ur.id_role = rp.id_role " +
            "JOIN permissions p ON rp.id_permission = p.id " +
            "WHERE u.username = ? AND u.is_active = true"
        );
        
        return userDetailsManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}