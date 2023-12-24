package com.springsecurity.client.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordResetToken {
    private static final int expiration=10;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Date expirationTime;
    private LocalDateTime creationTime;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_USER_PASSWORD_TOKEN")
    )
    private User user;
    public PasswordResetToken(User user,String token){
        super();
        this.token=token;
        this.user=user;
        creationTime=LocalDateTime.now();
        this.expirationTime=calculateExpirationDate(expiration);
    }

    public PasswordResetToken(String token) {
        super();
        this.token=token;
        this.expirationTime=calculateExpirationDate(expiration);
    }


    private Date calculateExpirationDate(int expiration){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(calendar.MINUTE,expiration);
        return new Date(calendar.getTime().getTime());
    }
}
