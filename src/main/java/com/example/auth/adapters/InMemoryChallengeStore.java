package com.example.auth.adapters;

import com.example.auth.domain.ChallengeStatus;
import com.example.auth.domain.MfaChallenge;
import com.example.auth.ports.ChallengeStore;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryChallengeStore implements ChallengeStore {

    private final Map<String, MfaChallenge> store = new ConcurrentHashMap<>();
    private final long expirySeconds;

    public InMemoryChallengeStore(long expirySeconds) {
        this.expirySeconds = expirySeconds;
    }

    @Override
    public MfaChallenge create(String username, String deviceId) {
        String challengeId = UUID.randomUUID().toString();
        return new MfaChallenge(
                challengeId,
                username,
                deviceId,
                Instant.now().plusSeconds(expirySeconds),
                ChallengeStatus.PENDING
        );
    }

    @Override
    public void save(MfaChallenge challenge) {
        store.put(challenge.challengeId(), challenge);
    }

    @Override
    public Optional<MfaChallenge> findById(String challengeId) {
        return Optional.ofNullable(store.get(challengeId));
    }

    @Override
    public void update(MfaChallenge challenge) {
        store.put(challenge.challengeId(), challenge);
    }

    @Override
    public void delete(String challengeId) {
        store.remove(challengeId);
    }
}

