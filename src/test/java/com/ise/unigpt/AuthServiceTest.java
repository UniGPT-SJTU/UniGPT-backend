package com.ise.unigpt;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.naming.AuthenticationException;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ise.unigpt.config.BasicConfig;
import com.ise.unigpt.exception.UserDisabledException;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.serviceimpl.AuthServiceImpl;
import com.ise.unigpt.utils.TestAuthFactory;
import com.ise.unigpt.utils.TestUserFactory;

public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthServiceImpl service;

    @InjectMocks
    private BasicConfig basicConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateAuthToken() throws Exception {
        AuthRepository authRepository = Mockito.mock(AuthRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        User user = TestUserFactory.createUser();
        Auth auth = TestAuthFactory.createAuth(user);
        Optional<Auth> optionalAuth = Optional.of(auth);
        AuthServiceImpl service = new AuthServiceImpl(authRepository, userRepository, basicConfig);
        when(authRepository.findByUser(user)).thenReturn(optionalAuth);
        when(authRepository.save(auth)).thenReturn(auth);

        String token = service.generateAuthToken(user);

        // Check if the token matches the UUID format
        assertTrue(token.matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    public void testJaccountLogin() throws Exception {
        AuthRepository authRepository = Mockito.mock(AuthRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        String code = "test";
        String expectedAccessToken = "testAccessToken";
        User expectedUser = TestUserFactory.createUser();
        Auth auth = TestAuthFactory.createAuth(expectedUser);
        Optional<Auth> optionalAuth = Optional.of(auth);
        AuthServiceImpl service = new AuthServiceImpl(authRepository, userRepository, basicConfig);
        when(authRepository.findByUser(auth.getUser())).thenReturn(optionalAuth);
        when(authRepository.save(auth)).thenReturn(auth);

        try {
            // 检查返回值是不是string
            assertTrue(service.jaccountLogin(code).matches(
                    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * public User getUserByToken(String token) {
     * // TODO: 登录请求应该抛出401(未授权)异常
     * Auth auth = authRepository.findByToken(token)
     * .orElseThrow(() -> new NoSuchElementException("Invalid token"));
     * User user = auth.getUser();
     * if (user.isDisabled()) {
     * throw new UserDisabledException("User is disabled");
     * }
     * return user;
     * }
     */
    @Test
    public void testGetUserByToken() throws Exception {
        AuthRepository authRepository = Mockito.mock(AuthRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        String token = "test";
        User user = TestUserFactory.createUser();
        assertNotNull(user.getId());
        Integer id = user.getId();
        Auth auth = TestAuthFactory.createAuth(user);
        Optional<Auth> optionalAuth = Optional.of(auth);
        Optional<User> optionalUser = Optional.of(user);
        AuthServiceImpl service = new AuthServiceImpl(authRepository, userRepository, basicConfig);
        when(authRepository.findByToken(token)).thenReturn(optionalAuth);
        when(authRepository.save(auth)).thenReturn(auth);
        when(authRepository.findByToken(token)).thenReturn(optionalAuth);
        when(authRepository.save(auth)).thenReturn(auth);
        when(userRepository.findById(user.getId())).thenReturn(optionalUser);

        try {
            assertEquals(id, service.getUserByToken(token).getId());
        } catch (UserDisabledException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
