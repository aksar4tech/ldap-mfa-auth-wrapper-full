package com.example.auth.ports;

import com.example.auth.domain.MfaChallenge;
import com.example.auth.domain.MfaVerification;

public interface MfaProvider {

    boolean verify(MfaVerification verification);

    void send(MfaChallenge challenge);
}

