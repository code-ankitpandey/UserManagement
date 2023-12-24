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
public class VerificationToken {
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
            foreignKey = @ForeignKey(name = "FK_USER_VERIFY_TOKEN")
    )
    private User user;
    public VerificationToken(User user,String token){
        super();
        this.token=token;
        this.user=user;
        this.expirationTime=calculateExpirationDate(expiration);
        this.creationTime=LocalDateTime.now();
    }

    public VerificationToken(String token) {
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
