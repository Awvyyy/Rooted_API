package com.example.demo.emailVerification;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {

    Optional<EmailToken> findByToken(String token);

    void deleteByUser(User user);
}
