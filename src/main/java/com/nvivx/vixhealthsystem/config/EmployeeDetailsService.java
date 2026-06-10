package com.nvivx.vixhealthsystem.config;

import com.nvivx.vixhealthsystem.mock.MockDatabase;
import com.nvivx.vixhealthsystem.model.person.employee.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * This service is used by Spring Security to load user details during login.
 * It connects our MockDatabase to Spring Security's authentication system.
 */
@Service
public class EmployeeDetailsService implements UserDetailsService {

    private final MockDatabase mockDatabase;

    public EmployeeDetailsService(MockDatabase mockDatabase) {
        this.mockDatabase = mockDatabase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = mockDatabase.findEmployeeByUsername(username);

        if (employee == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        if (!employee.isActive()) {
            throw new UsernameNotFoundException("User account is deactivated: " + username);
        }

        // The role already includes "ROLE_" prefix from the enum
        // So we don't add another "ROLE_"
        String roleName = employee.getRole().name(); // This returns "ROLE_STAFF_MANAGER"
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(roleName)
        );

        System.out.println("Loading user: " + username + " with role: " + roleName);

        return new User(
                employee.getUsername(),
                employee.getPassword(),
                authorities
        );
    }
}