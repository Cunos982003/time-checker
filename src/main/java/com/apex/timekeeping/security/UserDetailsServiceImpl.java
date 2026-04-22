package com.apex.timekeeping.security;

import com.apex.timekeeping.domain.entity.Employee;
import com.apex.timekeeping.domain.entity.UserAccount;
import com.apex.timekeeping.domain.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsernameWithEmployee(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Employee emp = account.getEmployee();
        String role = emp.getRole() != null ? emp.getRole().getRoleName() : "USER";
        String fullName = emp.getFullname() != null ? emp.getFullname() : username;

        return CustomUserDetails.builder()
                .accountId(account.getAccountId())
                .userId(emp.getUserId())
                .username(account.getUsername())
                .password(account.getPassword())
                .role(role)
                .fullName(fullName)
                .build();
    }
}
