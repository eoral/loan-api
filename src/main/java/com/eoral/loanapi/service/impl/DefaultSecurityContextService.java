package com.eoral.loanapi.service.impl;

import com.eoral.loanapi.service.SecurityContextService;
import com.eoral.loanapi.util.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class DefaultSecurityContextService implements SecurityContextService {

    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public boolean currentUserHasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminRole = "ROLE_" + Constants.ADMIN_ROLE; // Spring Security adds 'ROLE_' prefix by default.
        return (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(adminRole)));
    }
}
