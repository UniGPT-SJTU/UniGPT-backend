package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;

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
