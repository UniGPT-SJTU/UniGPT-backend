package com.ise.unigpt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * @brief 身份认证的类
 */
@Data
@Entity
@Table(name = "auth")
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Auth() {

    }
    public Auth(User user) {
        updateToken();
        this.user = user;
    }
    public void updateToken() {
        this.token = UUID.randomUUID().toString();
    }
}
