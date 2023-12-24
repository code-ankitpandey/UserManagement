package com.springsecurity.client.Controller;

import com.springsecurity.client.Entity.User;
import com.springsecurity.client.Entity.VerificationToken;
import com.springsecurity.client.Event.RegistrationCompleteEvent;
import com.springsecurity.client.Model.PasswordModel;
import com.springsecurity.client.Model.UserModel;
import com.springsecurity.client.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
public class RegistrationController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    @ResponseBody
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        if(user==null)return "This Mail is already Used";
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return "Verification Link Send to "+userModel.getEmail();
    }
    @ResponseBody
    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("Valid")) return "User Verified Succesfully";
        else return "Bad user";
    }

    @GetMapping("/resendVerificationToken")
    @ResponseBody
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        User user = verificationToken.getUser();
        resendVerifcationtokenMail(user, applicationUrl(request), verificationToken);
        return "Verification Link Send "+user.getEmail();
    }

    @PostMapping("/resetPassword")
    @ResponseBody
    public String resetPassword(@RequestBody PasswordModel passwordModel, HttpServletRequest request) {
        User user = userService.findUserByEmail(passwordModel.getEmail());
        if (user != null) {
            String token = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user, token);
            passwordResetTokenMail(user, applicationUrl(request), token);
            return "reset link send to the mail "+user.getEmail();
        }
        return "User does not exits";
        }
        @PostMapping("/savePassword")
        public String savePassword(@RequestParam("token") String token, @RequestBody PasswordModel passwordModel) {
            String result = userService.validatePasswordResetToken(token);
            if (!result.equalsIgnoreCase("valid")) {
                return "Invalid Token";
            }
            Optional<User> user = userService.getUserByPasswordResetToken(token);
            if (user.isPresent()) {
                userService.changePassword(user.get(), passwordModel.getNewPassword());

                return "Password reset Successful";
            } else {
                return "Invalid Token";
            }
        }

    @PostMapping("/changePassword")
    @ResponseBody
    public String changePassword(@RequestBody PasswordModel passwordModel){
        User user=userService.findUserByEmail(passwordModel.getEmail());
        if (user==null){
            System.out.println("user doesnt exits");
            return "User doesn't exits";
        }
        if(!userService.checkIfValidOldPassword(user,passwordModel.getOldPassword())){
            System.out.println("Invalid old Password");
            return  "Invalid old password";
        }
        userService.changePassword(user,passwordModel.getNewPassword());
        System.out.println("password changed succesfully");
        return "password Changed Successfully";
    }

    private void passwordResetTokenMail(User user, String applicationUrl, String token) {
        String url =
                applicationUrl
                        + "/savePassword.html?token="
                        + token;
        userService.sendEmail(user.getEmail(),"Password Reset","Click on the link to reset your password:"+url);
    }

    private void resendVerifcationtokenMail(User user, String applicationUrl, VerificationToken token) {
        String url = applicationUrl +
                "/verifyRegistration?token="
                + token.getToken();
        log.info("Click the link to verify your account: {}" + url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" +
                request.getServerName() +
                ":" +
                request.getServerPort() +
                request.getContextPath();
    }
}
