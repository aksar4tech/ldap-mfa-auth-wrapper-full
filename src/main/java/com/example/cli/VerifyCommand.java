
package com.example.cli;

import com.example.auth.application.MfaVerificationService;
import com.example.auth.domain.AuthResult;
import com.example.auth.domain.MfaVerification;
import picocli.CommandLine;

@CommandLine.Command(name = "verify", description = "Verify MFA challenge")
public class VerifyCommand implements Runnable {

    @CommandLine.Option(names = "--challengeId", required = true)
    String challengeId;

    @CommandLine.Option(names = "--token", required = true)
    String token;

    @CommandLine.Option(names = "--deviceId", required = true)
    String deviceId;

    @Override
    public void run() {
        MfaVerificationService service = SpringContextHolder.getBean(MfaVerificationService.class);

        AuthResult result = service.verify(new MfaVerification(challengeId, token, deviceId));

        System.out.println(result);
    }

}
