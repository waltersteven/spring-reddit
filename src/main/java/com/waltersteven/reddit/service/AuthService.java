package com.waltersteven.reddit.service;

import com.waltersteven.reddit.dto.RegisterRequest;
import com.waltersteven.reddit.exceptions.SpringRedditException;
import com.waltersteven.reddit.model.NotificationEmail;
import com.waltersteven.reddit.model.User;
import com.waltersteven.reddit.model.VerificationToken;
import com.waltersteven.reddit.repository.UserRepository;
import com.waltersteven.reddit.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    // These instance variables will be initialized by Lombok (@AllArgsConstructor)
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    @Transactional
    public void signup(RegisterRequest registerRequest) {
        User user = new User();

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        userRepository.save(user);

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail(
                "Please activate your Account",
                user.getEmail(),
                "Thank your for signing up to Reddit. Please click on the link below to activate your account " +
                        "http://localhost:8080/api/auth/accountVerification/" + token
        ));

    }

    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);

        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));

        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new SpringRedditException("User not found with name: " + username)
        );

        user.setEnabled(true);

        userRepository.save(user);
    }
}
