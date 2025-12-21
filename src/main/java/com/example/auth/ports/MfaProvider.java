package com.example.auth.ports;

import com.example.auth.domain.MfaChallenge;
import com.example.auth.domain.MfaVerification;
import com.example.auth.domain.UserIdentity;

public interface MfaProvider {

    MfaChallenge initiate(UserIdentity user);

    boolean verify(MfaVerification verification);
}

