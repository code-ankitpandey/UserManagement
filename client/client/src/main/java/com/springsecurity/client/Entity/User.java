package com.springsecurity.client.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private String email;

    private String firstName;
    private String lastName;


    @Column(length = 60)
    private String password;

    private String role;
    private boolean enabled = false;
}
