package com.ise.unigpt.service;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AuthService {
    private AuthRepository authRepository;
    private UserRepository userRepository;

    public AuthService(AuthRepository authRepository, UserRepository userRepository) {
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    /**
     * @brief 用户登录函数
     * @param dto 登录请求
     * @return 若登录成功，返回更新后的token
     * @throws AuthenticationException 登录异常
     */
    public String login(LoginRequestDTO dto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByName(dto.getUsername());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(user.getPassword().equals(dto.getPassword())) {
                return generateAuthToken(user);
            }
        }
        throw new AuthenticationException("Invalid username or password");
    }
    public void register(RegisterRequestDTO dto) {
        if(userRepository.findByName(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        userRepository.save(new User(dto));
    }

    /**
     * @brief 生成认证的令牌
     * @brief 为用户生成一个唯一的令牌。如果用户已经拥有令牌，则更新令牌；如果用户没有令牌，则生成一个新的令牌。
     * @param user 用户
     * @return 生成的令牌
     */
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
}
