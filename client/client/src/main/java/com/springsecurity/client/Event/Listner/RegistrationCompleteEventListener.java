package com.springsecurity.client.Event.Listner;

import com.springsecurity.client.Entity.User;
import com.springsecurity.client.Event.RegistrationCompleteEvent;
import com.springsecurity.client.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    @Autowired
    private UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(token,user);
        String url=event.getApplicationUrl()+"/verifyRegistration?token="+token;
        //send verification email method
        log.info("Click the link to verify the account: {}",url);
        userService.sendEmail(user.getEmail(),"Verify Your Account","verify your account by clicking the link: "+url);

    }
}
