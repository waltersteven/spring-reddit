package com.waltersteven.reddit.repository;

import com.waltersteven.reddit.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);
}
