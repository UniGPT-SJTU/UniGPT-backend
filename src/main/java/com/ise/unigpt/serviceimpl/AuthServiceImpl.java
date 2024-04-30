package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.dto.JaccountResponseDTO;

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

    public String login(LoginRequestDTO dto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByName(dto.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getPassword().equals(dto.getPassword())) {
                return generateAuthToken(user);
            }
        }
        throw new AuthenticationException("Invalid username or password");
    }

    public void register(RegisterRequestDTO dto) {
        if (userRepository.findByName(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        userRepository.save(new User(dto));
    }

    private String generateAuthToken(User user) {
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
        return auth.getUser();
    }

    public String requestAccessToken(String code) throws AuthenticationException {
        String client_id = "ov3SLrO4HyZSELxcHiqS";
        String client_secret = "B9919DDA3BD9FBF7ADB9F84F67920D8CB6528620B9586D1C";
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = null;
        try {
            response = Unirest.post("http://jaccount.sjtu.edu.cn/oauth2/token")
                    .header("Authorization",
                            "Basic b3YzU0xyTzRIeVpTRUx4Y0hpcVM6Qjk5MTlEREEzQkQ5RkJGN0FEQjlGODRGNjc5MjBEOENCNjUyODYyMEI5NTg2RDFD")
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Accept", "*/* ")
                    .header("Host", "jaccount.sjtu.edu.cn")
                    .header("Connection", "keep-alive")
                    .field("grant_type", "authorization_code")
                    .field("code", code)
                    .field("client_id", client_id)
                    .field("client_secret", client_secret)
                    .field("redirect_uri", "http://localhost:3000/login")
                    .asString();
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        JSONObject responseBody = null;
        try {
            responseBody = new JSONObject(response.getBody());
        } catch (JSONException e) {
            throw new AuthenticationException("Request access token failed");
        }
        String accessToken ;
        try{
            accessToken = responseBody.getString("access_token");
        }
        catch (JSONException e){
            throw new AuthenticationException("Request access token failed");
        }
        return accessToken;
    }

    public String jaccountLogin(String code) throws AuthenticationException {
        String accessToken;
        try {
            accessToken = requestAccessToken(code);
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Jaccount login failed");
        }

        User user = sendGetRequest("https://api.sjtu.edu.cn/v1/me/profile?access_token=" + accessToken);
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

    private User sendGetRequest(String urlStr) throws AuthenticationException {
        try {
            // System.out.println("urlStr: " + urlStr);
            Unirest.setTimeouts(300, 3000);
            HttpResponse<String> response = Unirest.get(urlStr)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Accept", "*/*")
                    .header("Host", "api.sjtu.edu.cn")
                    .header("Connection", "keep-alive")
                    .asString();

            // System.out.println("Response status: " + response.getStatus());
            // System.out.println("Response body: " + response.getBody());

            if (response.getStatus() == HttpURLConnection.HTTP_OK) {
                ObjectMapper mapper = new ObjectMapper();
                JaccountResponseDTO jaccountResponse = mapper.readValue(response.getBody(), JaccountResponseDTO.class);

                return new User(jaccountResponse.getUsers().get(0));
            } else {
                // System.out.println("GET request not worked");
                throw new AuthenticationException("GET request not worked");
            }

        } catch (Exception e) {
            // System.out.println("Exception: " + e.getMessage());
            throw new AuthenticationException("Sending GET request failed");
        }
    }
}
