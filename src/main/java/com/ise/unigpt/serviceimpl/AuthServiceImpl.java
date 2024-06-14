package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.dto.JaccountResponseDTO;
import com.ise.unigpt.exception.UserDisabledException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.json.JSONObject;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;

import java.net.HttpURLConnection;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public String generateAuthToken(User user) {
        Optional<Auth> optionalAuth = authRepository.findByUser(user);
        Auth auth;
        if (optionalAuth.isPresent()) {
            // 用户已经拥有令牌
            auth = optionalAuth.get();
            auth.updateToken();
        } else {
            // 用户没有令牌
            auth = new Auth(user);
        }

        authRepository.save(auth);
        return auth.getToken();
    }

    public User getUserByToken(String token) {
        // TODO: 登录请求应该抛出401(未授权)异常
        Auth auth = authRepository.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Invalid token"));
        User user = auth.getUser();
        if (user.isDisabled()) {
            throw new UserDisabledException("User is disabled");
        }
        return user;
    }

    public String jaccountLogin(String code) throws AuthenticationException {
        String accessToken;

        accessToken = "aaa";

        User user = new User();
        // System.out.println("user" + user);
        // 查找是否已经注册
        Optional<User> optionalUser = userRepository.findByAccount(user.getAccount());
        if (optionalUser.isPresent()) {
            return generateAuthToken(optionalUser.get());
        }
        userRepository.save(user);
        String token = generateAuthToken(user);
        System.out.println("token" + token);
        return token;
    }

}
