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

/*    @Override
    public void run() {
        try (var ctx = new AnnotationConfigApplicationContext("com.example.auth")) {
            AuthenticationService service = ctx.getBean(AuthenticationService.class);
            var result = service.authenticateAsync(new AuthRequest(username, password.toCharArray()));
            System.out.println(result);
        }
    }*/

    @Override
    public void run() {
        AuthenticationService service = SpringContextHolder.getBean(AuthenticationService.class);

        AuthResult result = service.authenticateAsync(new AuthRequest(username, password.toCharArray()));

        System.out.println(result);
    }

}
