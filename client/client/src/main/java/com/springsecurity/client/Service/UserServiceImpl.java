package com.springsecurity.client.Service;

import com.springsecurity.client.Entity.PasswordResetToken;
import com.springsecurity.client.Entity.User;
import com.springsecurity.client.Entity.VerificationToken;
import com.springsecurity.client.Model.UserModel;
import com.springsecurity.client.Repository.PasswordResetTokenRepository;
import com.springsecurity.client.Repository.UserRepository;
import com.springsecurity.client.Repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid ";
        }

        User user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (passwordResetToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }
        return "valid";

    }

    @Override
    public User findUserByEmail(String email) {

        return  userRepository.findByEmail(email);
    }

    @Override
    public User registerUser(UserModel userModel) {
        Optional<User>tempUser= Optional.ofNullable(userRepository.findByEmail(userModel.getEmail()));
        if (tempUser.isPresent())return null;
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(String token, User user) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null) return "Invalid Token";

        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() < 0) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";

    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken=passwordResetTokenRepository.getPasswordResetTokenByUser(user);
        if(passwordResetToken!=null){
            passwordResetTokenRepository.delete(passwordResetToken);
        }
        PasswordResetToken passwordResetToken1 = new PasswordResetToken(user, token);
        passwordResetToken1.setCreationTime(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken1);
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changePassword(User user, String newPassword) {
      user.setPassword(passwordEncoder.encode(newPassword));
      if (!user.isEnabled()){
          user.setEnabled(true);
      }
      userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user,String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }
    @Override
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
        System.out.println("mail send succesfully");
    }
}

