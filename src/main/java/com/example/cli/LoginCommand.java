package com.example.cli;

import com.example.auth.application.AuthenticationService;
import com.example.auth.domain.AuthRequest;
import com.example.auth.domain.AuthResult;
import picocli.CommandLine;

@CommandLine.Command(name = "login", description = "LDAP login")
public class LoginCommand implements Runnable {

    @CommandLine.Option(names = "--username", required = true)
    String username;

    @CommandLine.Option(names = "--password", required = true)
    String password;

    @CommandLine.Option(names = "--deviceId", required = true)
    String deviceId;


    @Override
    public void run() {
        AuthenticationService service = SpringContextHolder.getBean(AuthenticationService.class);

        AuthResult result = service.authenticateAsync(new AuthRequest(username, password.toCharArray(), deviceId));

        System.out.println(result);
    }

}
