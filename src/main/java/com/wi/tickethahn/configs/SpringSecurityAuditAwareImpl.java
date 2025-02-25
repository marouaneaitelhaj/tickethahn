package com.wi.tickethahn.configs;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.wi.tickethahn.entities.User;
import com.wi.tickethahn.services.inter.AuthenticationService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class SpringSecurityAuditAwareImpl {

    private final AuthenticationService authenticationService;

    private final ModelMapper modelMapper;

    public User getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationService.getUserDbUser(authentication.getName());
    }
}