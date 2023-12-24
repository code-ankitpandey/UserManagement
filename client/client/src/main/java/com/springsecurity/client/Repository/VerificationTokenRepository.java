package com.springsecurity.client.Repository;

import com.springsecurity.client.Entity.PasswordResetToken;
import com.springsecurity.client.Entity.User;
import com.springsecurity.client.Entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
    VerificationToken getVerificationTokenByUser(User user);

    void deleteByCreationTimeAfter(LocalDateTime tenMinutesAgo);
}
