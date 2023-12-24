package com.springsecurity.client.Repository;

import com.springsecurity.client.Entity.PasswordResetToken;
import com.springsecurity.client.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {

      PasswordResetToken findByToken(String token) ;
      PasswordResetToken getPasswordResetTokenByUser(User user);



}
