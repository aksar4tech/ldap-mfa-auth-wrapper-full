package com.example.auth.adapters;

import com.example.auth.domain.UserIdentity;
import com.example.auth.ports.LdapAuthenticationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Optional;

public class OpenLdapAuthenticationAdapter implements LdapAuthenticationPort {

    private static final Logger log = LoggerFactory.getLogger(OpenLdapAuthenticationAdapter.class);

    private final LdapTemplate ldapTemplate;
    private final String userSearchBase;
    private final String emailAttribute;

    public OpenLdapAuthenticationAdapter(
            LdapTemplate ldapTemplate,
            String userSearchBase,
            String emailAttribute
    ) {
        this.ldapTemplate = ldapTemplate;
        this.userSearchBase = userSearchBase;
        this.emailAttribute = emailAttribute;
    }

    @Override
    public Optional<UserIdentity> authenticate(String username, char[] password) {
        try {
            EqualsFilter filter = new EqualsFilter("uid", username);

            // 1. Authenticate user credentials
            boolean authenticated = ldapTemplate.authenticate(
                    userSearchBase,
                    filter.encode(),
                    new String(password)
            );

            if (!authenticated) {
                return Optional.empty();
            }

            // 2. Fetch user attributes
            List<UserIdentity> users = ldapTemplate.search(
                    userSearchBase,
                    filter.encode(),
                    new AttributesMapper<>() {
                        @Override
                        public UserIdentity mapFromAttributes(Attributes attrs) throws NamingException {

                            String dn = attrs.get("distinguishedName") != null
                                    ? attrs.get("distinguishedName").get().toString()
                                    : null;

                            String email = attrs.get(emailAttribute) != null
                                    ? attrs.get(emailAttribute).get().toString()
                                    : null;

                            return new UserIdentity(username, dn, email);
                        }
                    }
            );

            return users.stream().findFirst();
        } catch (AuthenticationException ae) {
            log.info("Error while authenticating the user: {}, message: {}", username, ae.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error while authenticating the user: {}, message: {}", username, e.getMessage());
            throw new RuntimeException("Authenticating user failed", e);
        }
    }

    @Override
    public boolean isAccountLocked(String username) {
        // OpenLDAP lockout is schema-specific
        return false;
    }
}
