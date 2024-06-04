package com.ise.unigpt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ise.unigpt.dto.GetUsersOkResponseDTO;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;
import com.ise.unigpt.serviceimpl.UserServiceImpl;
import com.ise.unigpt.utils.TestUserFactory;

public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createAdmin());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));

        // Act
        GetUsersOkResponseDTO result = null;
        try {
            result = userService.getUsers(0, 10, token, "latest", "");
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (result == null) {
            // alert
            return;
        }
        // Assert
        assertEquals(3, result.getUsers().size());
        assertEquals("user1", result.getUsers().get(0).getName());

    }

    @Test
    public void testGetUsers_unauthorized() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));

        // Act
        GetUsersOkResponseDTO result = null;
        try {
            result = userService.getUsers(0, 10, token, "latest", "");
        } catch (AuthenticationException e) {
            // assert
            assertEquals("Unauthorized to get users", e.getMessage());
        }

    }

    @Test
    public void testSetBanUser() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createAdmin());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));
        when(repository.findById(1)).thenReturn(java.util.Optional.of(TestUserFactory.createUser()));
        // Act
        try {
            userService.setBanUser(1, token, true);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Assert
        assertEquals(userService.findUserById(1).isDisabled(), true);
    }

    @Test
    public void testSetBanUser_unauthorized() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));
        when(repository.findById(1)).thenReturn(java.util.Optional.of(TestUserFactory.createUser()));
        // Act
        try {
            userService.setBanUser(1, token, true);
        } catch (AuthenticationException e) {
            // assert
            assertEquals("Unauthorized to ban user", e.getMessage());
        }

    }

    @Test
    public void testGetBanState() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);

        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createAdmin());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));
        when(repository.findById(1)).thenReturn(java.util.Optional.of(TestUserFactory.createUser()));
        // Act
        boolean result = true;
        try {
            result = userService.getBanState(1, token);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        // Assert
        assertEquals(false, result);
    }

    @Test
    public void testGetBanState_unauthorized() {
        String token = "token";
        // mock auth service
        AuthService authService = Mockito.mock(AuthService.class);
        UserRepository repository = Mockito.mock(UserRepository.class);

        UserService userService = new UserServiceImpl(repository, authService);
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        when(repository.findAll()).thenReturn(
                List.of(TestUserFactory.createUser(), TestUserFactory.createUser2(), TestUserFactory.createUser3()));
        when(repository.findById(1)).thenReturn(java.util.Optional.of(TestUserFactory.createUser()));
        // Act
        boolean result = true;
        try {
            result = userService.getBanState(1, token);
        } catch (AuthenticationException e) {
            // assert
            assertEquals("Unauthorized to get ban state", e.getMessage());
        }
    }

}
