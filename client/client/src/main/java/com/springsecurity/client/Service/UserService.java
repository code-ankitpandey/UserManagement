package com.springsecurity.client.Service;

import com.springsecurity.client.Entity.User;
import com.springsecurity.client.Entity.VerificationToken;
import com.springsecurity.client.Model.UserModel;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {
    String validatePasswordResetToken(String token);

    User findUserByEmail(String email) ;

    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(String token, User user);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void   createPasswordResetTokenForUser(User user, String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changePassword(User user, String newPassword);

    boolean checkIfValidOldPassword(User user,String oldPassword);
    public void sendEmail(String to, String subject, String text);
}
