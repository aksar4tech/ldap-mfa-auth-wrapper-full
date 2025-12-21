package com.example.auth.ports;

import com.example.auth.domain.MfaChallenge;

import java.util.Optional;

public interface ChallengeStore {

    void save(MfaChallenge challenge);

    Optional<MfaChallenge> findById(String challengeId);

    void update(MfaChallenge challenge);

    void delete(String challengeId);
}

