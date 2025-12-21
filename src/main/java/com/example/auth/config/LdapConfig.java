package com.example.auth.config;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import java.util.HashMap;
import java.util.Map;

public class LdapConfig {

    public static LdapContextSource contextSource(
            String url,
            String baseDn,
            String bindDn,
            String bindPassword,
            String poolEnabled,
            String connectTimeout,
            String readTimeout
    ) {
        LdapContextSource source = new LdapContextSource();
        source.setUrl(url);
        source.setBase(baseDn);
        source.setUserDn(bindDn);
        source.setPassword(bindPassword);

        Map<String, Object> env = new HashMap<>();
        env.put("com.sun.jndi.ldap.connect.pool", poolEnabled);
        env.put("com.sun.jndi.ldap.connect.timeout", connectTimeout);
        env.put("com.sun.jndi.ldap.read.timeout", readTimeout);
        source.setBaseEnvironmentProperties(env);

        source.afterPropertiesSet();
        return source;
    }

}

