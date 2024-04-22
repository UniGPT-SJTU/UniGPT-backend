package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.dto.JaccountUserDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.oltu.oauth2.common.token.OAuthToken;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;

import edu.sjtu.oauth.applicationToolkit.OAuth2Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public String jaccountLogin(String code) throws AuthenticationException {

        String client_id = "ov3SLrO4HyZSELxcHiqS";
        String client_secret = "B9919DDA3BD9FBF7ADB9F84F67920D8CB6528620B9586D1C";
        OAuthToken token;
        try {
            token =  OAuth2Util.getToken(client_id, client_secret, "profile");
        } catch (Exception e) {
            throw new AuthenticationException("Jaccount login failed");
        }
        User user = sendGetRequest("https://api.jaccount.sjtu.edu.cn/v1/me?access_token=" + token.getAccessToken());
        return generateAuthToken(user);
    }

    

    private User sendGetRequest(String urlStr) throws AuthenticationException {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                ObjectMapper mapper = new ObjectMapper();
                JaccountUserDTO res = mapper.readValue(response.toString(), JaccountUserDTO.class);
                return res.getUsers().get(0); // return the first user
            } else {
                throw new AuthenticationException("GET request not worked");
            }

        } catch (Exception e) {
            throw new AuthenticationException("Sending GET request failed");
        }
    }
}

