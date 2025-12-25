package com.example.auth.config;

import com.example.auth.adapters.*;
import com.example.auth.application.AuthenticationService;
import com.example.auth.application.MfaVerificationService;
import com.example.auth.ports.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.LdapTemplate;


@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.baseDn}")
    private String baseDn;

    @Value("${ldap.bindDn}")
    private String bindDn;

    @Value("${ldap.bindPassword}")
    private String bindPassword;

    @Value("${ldap.pool.enabled}")
    private String poolEnabled;

    @Value("${ldap.connect.timeout.ms}")
    private String connectTimeout;

    @Value("${ldap.read.timeout.ms}")
    private String readTimeout;


    @Value("${smtp.host}")
    private String smtpHost;

    @Value("${smtp.port}")
    private int smtpPort;

    @Value("${smtp.username}")
    private String smtpUsername;

    @Value("${smtp.password}")
    private String smtpPassword;

    @Value("${smtp.from}")
    private String smtpFrom;


    @Value("${access.token.ttl.seconds}")
    public long accessTokenTtlSeconds;

    @Value("${access.token.secret}")
    private String accessTokenSecret;


    @Value("${mfa.magicLink.expiry.seconds}")
    public long mfaTokenTtlSeconds;

    @Value("${mfa.token.secret}")
    private String mfaTokenSecret;

    @Value("${sec.maxattempts}")
    private int maxAttempts;

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(LdapConfig.contextSource(ldapUrl, baseDn, bindDn, bindPassword, poolEnabled, connectTimeout, readTimeout));
    }

    @Bean
    public LdapAuthenticationPort ldapAuthPort() {
        return new OpenLdapAuthenticationAdapter(ldapTemplate(), "ou=users", "mail");
    }

    @Bean
    public MfaTokenService tokenService() {
        return new JwtMfaTokenServiceAdapter(mfaTokenSecret.getBytes(), mfaTokenTtlSeconds);
    }

    @Bean
    public MfaProvider mfaProvider() {
        return new EmailMagicLinkMfaProvider(emailSender(), tokenService(), mfaTokenTtlSeconds);
    }

    @Bean
    public ChallengeStore challengeStore() {
        return new InMemoryChallengeStore(mfaTokenTtlSeconds);
    }

    @Bean
    public RateLimiter rateLimiter() {
        return new InMemoryRateLimiter(maxAttempts);
    }

    @Bean
    public AuditLogger auditLogger() {
        return new Slf4jAuditLoggerAdapter();
    }

    @Bean
    public AuthenticationService authenticationService() {
        return new AuthenticationService(
                ldapAuthPort(),
                mfaProvider(),
                challengeStore(),
                auditLogger(),
                rateLimiter()
        );
    }

    @Bean
    public MfaVerificationService mfaVerificationService() {
        return new MfaVerificationService(
                mfaProvider(),
                challengeStore(),
                auditLogger(),
                accessTokenService()
        );
    }

    @Bean
    public AccessTokenService accessTokenService() {
        return new JwtAccessTokenServiceAdapter(accessTokenSecret.getBytes(), accessTokenTtlSeconds);
    }

    @Bean
    public EmailSender emailSender() {
        return new SmtpEmailSenderAdapter(
                smtpHost,
                smtpPort,
                smtpUsername,
                smtpPassword,
                smtpFrom
        );
    }

}
