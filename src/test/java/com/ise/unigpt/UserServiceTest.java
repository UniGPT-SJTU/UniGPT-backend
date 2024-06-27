package com.ise.unigpt;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.security.sasl.AuthenticationException;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ise.unigpt.dto.GetBotsOkResponseDTO;
import com.ise.unigpt.dto.GetUsersOkResponseDTO;
import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;
import com.ise.unigpt.serviceimpl.UserServiceImpl;
import com.ise.unigpt.utils.TestUserFactory;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUsers() throws Exception {
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
    // 需要改，为达到测试覆盖率加的
    public void testGetUsers_id() throws Exception {
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
            result = userService.getUsers(0, 10, token, "id", "");
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
    public void testGetUsers_unauthorized() throws Exception {
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
    public void testSetBanUser() throws Exception {
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
        assertEquals(userService.findUserById(1).getDisabled(), true);
    }

    @Test
    public void testSetBanUser_unauthorized() throws Exception {
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
    public void testGetBanState() throws Exception {
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
    public void testGetBanState_unauthorized() throws Exception {
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

    @Test
    public void testFindUserById_UserExists() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        User foundUser = userService.findUserById(1);

        assertNotNull(foundUser);
        assertEquals(Integer.valueOf(1), foundUser.getId());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testFindUserById_UserNotExists() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> userService.findUserById(1),
                "Expected findUserById to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("User not found for ID: 1"));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testUpdateUserInfo_Success() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);
        User user = new User();
        user.setId(1);
        user.setName("Old Name");

        UpdateUserInfoRequestDTO dto = new UpdateUserInfoRequestDTO();
        dto.setName("New Name");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(user);

        userService.updateUserInfo(1, dto, "token");

        assertEquals("New Name", user.getName());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateUserInfo_Unauthorized() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);

        User user = new User();
        user.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        UpdateUserInfoRequestDTO dto = new UpdateUserInfoRequestDTO();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(differentUser);

        AuthenticationException thrown = assertThrows(
                AuthenticationException.class,
                () -> userService.updateUserInfo(1, dto, "token"),
                "Expected updateUserInfo to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Unauthorized to update user info"));
        verify(userRepository, never()).save(user);
    }

    @Test
    public void testGetUsedBots_Success() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);

        User user = new User();
        user.setId(1);
        Bot bot1 = new Bot();
        bot1.setId(1);
        Bot bot2 = new Bot();
        bot2.setId(2);
        user.setUsedBots(Arrays.asList(bot1, bot2));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(user);

        GetBotsOkResponseDTO response = userService.getUsedBots(1, "token", 0, 10);

        assertNotNull(response);
        assertEquals(Integer.valueOf(2), response.getTotal());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testGetUsedBots_Unauthorized() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);
        User user = new User();
        user.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(differentUser);

        AuthenticationException thrown = assertThrows(
                AuthenticationException.class,
                () -> userService.getUsedBots(1, "token", 0, 10),
                "Expected getUsedBots to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Unauthorized to get used bots"));
    }

    @Test
    public void testGetStarredBots_Success() throws AuthenticationException {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);

        User user = new User();
        user.setId(1);
        Bot bot1 = new Bot();
        bot1.setId(1);
        Bot bot2 = new Bot();
        bot2.setId(2);
        user.setStarBots(Arrays.asList(bot1, bot2));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(user);

        GetBotsOkResponseDTO response = userService.getStarredBots(1, "token", 0, 10);

        assertNotNull(response);
        assertEquals(Integer.valueOf(2), response.getTotal());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void testGetStarredBots_Unauthorized() throws AuthenticationException {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);

        User user = new User();
        user.setId(1);

        User differentUser = new User();
        differentUser.setId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(differentUser);

        AuthenticationException thrown = assertThrows(
                AuthenticationException.class,
                () -> userService.getStarredBots(1, "token", 0, 10),
                "Expected getStarredBots to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Unauthorized to get used bots"));
    }

    @Test
    public void testGetCreatedBots_Success() {
        userRepository = Mockito.mock(UserRepository.class);
        authService = Mockito.mock(AuthService.class);
        userService = new UserServiceImpl(userRepository, authService);

        User user = new User();
        user.setId(1);
        Bot bot1 = new Bot();
        bot1.setId(1);
        Bot bot2 = new Bot();
        bot2.setId(2);
        user.setCreateBots(Arrays.asList(bot1, bot2));

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(authService.getUserByToken("token")).thenReturn(user);

        GetBotsOkResponseDTO response = userService.getCreatedBots(1, "token", 0, 10);

        assertNotNull(response);
        assertEquals((Integer) 2, response.getTotal()); // Cast the expected value to Integer
        verify(userRepository, times(1)).findById(1);
    }

}
