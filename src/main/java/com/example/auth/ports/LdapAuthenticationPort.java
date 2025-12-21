package com.example.auth.ports;

import com.example.auth.domain.UserIdentity;

import java.util.Optional;

public interface LdapAuthenticationPort {

    Optional<UserIdentity> authenticate(String username, char[] password);

    boolean isAccountLocked(String username);
}

