package com.ise.unigpt;

import com.ise.unigpt.controller.UserController;
import com.ise.unigpt.dto.*;
import com.ise.unigpt.exception.UserDisabledException;
import com.ise.unigpt.model.User;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;

import com.ise.unigpt.utils.TestUserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.security.sasl.AuthenticationException;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserProfile() throws Exception {
        User user = TestUserFactory.createUser();
        when(userService.findUserById(1)).thenReturn(user);
        ResponseEntity<Object> response = controller.getUserProfile(1);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUserProfileNotFound() throws Exception {
        when(userService.findUserById(1)).thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.getUserProfile(1);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetMyProfile() throws Exception {
        User user = TestUserFactory.createUser();
        when(userService.findUserById(1)).thenReturn(user);
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseEntity<Object> response = controller.getMyProfile("token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetMyProfileNotFound() {
        when(authService.getUserByToken("token")).thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.getMyProfile("token");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetMyProfileForbidden() {
        when(authService.getUserByToken("token")).thenThrow(new UserDisabledException("User is disabled"));
        ResponseEntity<Object> response = controller.getMyProfile("token");
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testGetMyProfileUnauthorized() {
        when(authService.getUserByToken("token")).thenThrow(new RuntimeException("Unauthorized"));
        ResponseEntity<Object> response = controller.getMyProfile("token");
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        UpdateUserInfoRequestDTO updateUserInfoRequestDTO = new UpdateUserInfoRequestDTO();
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseEntity<Object> response = controller.updateUserProfile(1, "token", updateUserInfoRequestDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateUserProfileUnauthorized() throws AuthenticationException {
        UpdateUserInfoRequestDTO updateUserInfoRequestDTO = new UpdateUserInfoRequestDTO();
        when(userService.updateUserInfo(1, updateUserInfoRequestDTO, "token"))
                .thenThrow(new AuthenticationException("Unauthorized"));
        ResponseEntity<Object> response = controller.updateUserProfile(1, "token", updateUserInfoRequestDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testUpdateUserProfileNotFound() throws AuthenticationException {
        UpdateUserInfoRequestDTO updateUserInfoRequestDTO = new UpdateUserInfoRequestDTO();
        when(userService.updateUserInfo(1, updateUserInfoRequestDTO, "token"))
                .thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.updateUserProfile(1, "token", updateUserInfoRequestDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUsedBots() throws Exception {
        User user = TestUserFactory.createUser();
        when(userService.findUserById(1)).thenReturn(user);
        when(authService.getUserByToken("token")).thenReturn(user);
        GetBotsOkResponseDTO responseDTO = new GetBotsOkResponseDTO(0, Collections.emptyList());
        when(userService.getUsedBots(1, "token", 0, 20)).thenReturn(responseDTO);
        ResponseEntity<Object> response = controller.getUsedBots(1, "token", 0, 20);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUsedBotsUnauthorized() throws AuthenticationException {
        when(userService.getUsedBots(1, "token", 0, 20)).thenThrow(new AuthenticationException("Unauthorized"));
        ResponseEntity<Object> response = controller.getUsedBots(1, "token", 0, 20);
        System.out.println(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetUsedBotsNotFound() throws AuthenticationException {
        when(userService.getUsedBots(1, "token", 0, 20)).thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.getUsedBots(1, "token", 0, 20);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetStarredBots() throws Exception {
        User user = TestUserFactory.createUser();
        when(userService.findUserById(1)).thenReturn(user);
        when(authService.getUserByToken("token")).thenReturn(user);
        GetBotsOkResponseDTO responseDTO = new GetBotsOkResponseDTO(0, Collections.emptyList());
        when(userService.getStarredBots(1, "token", 0, 20)).thenReturn(responseDTO);
        ResponseEntity<Object> response = controller.getStarredBots(1, "token", 0, 20);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testGetStarredBotsNotFound() throws AuthenticationException {
        when(userService.getStarredBots(1, "token", 0, 20)).thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.getStarredBots(1, "token", 0, 20);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetCreatedBots() throws Exception{
        GetBotsOkResponseDTO responseDTO = new GetBotsOkResponseDTO(0, Collections.emptyList());
        when(userService.getCreatedBots(1, "token", 0, 20)).thenReturn(responseDTO);
        ResponseEntity<Object> response = controller.getCreatedBots(1, "token", 0, 20);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetCreatedBotsNotFound() throws Exception{
        when(userService.getCreatedBots(1, "token", 0, 20)).thenThrow(new NoSuchElementException("User not found"));
        ResponseEntity<Object> response = controller.getCreatedBots(1, "token", 0, 20);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetUsers() throws Exception {
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        GetUsersOkResponseDTO responseDTO = new GetUsersOkResponseDTO(0, Collections.emptyList());
        when(userService.getUsers(0, 20, "token", "id", "keyword")).thenReturn(responseDTO);
        ResponseEntity<Object> response = controller.getUsers(0, 20, "token", "id", "keyword");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetUsersUnauthorized() throws Exception {
        when(userService.getUsers(0, 20, "token", "keyword", "")).thenThrow(new AuthenticationException("Unauthorized"));
        ResponseEntity<Object> response = controller.getUsers(0, 20, "keyword", "","token");
        System.out.println(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testDisableUser() throws Exception {
        User user = TestUserFactory.createUser();
        when(userService.findUserById(1)).thenReturn(user);
        ResponseEntity<Object> response = controller.disableUser(1, "token", true);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDisableUserUnauthorized() throws AuthenticationException {
        when(userService.setBanUser(1, "token", true)).thenThrow(new AuthenticationException("Unauthorized"));
        ResponseEntity<Object> response = controller.disableUser(1, "token", true);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testIsUserDisabled() throws AuthenticationException {
        when(userService.getBanState(1, "token")).thenReturn(true);
        ResponseEntity<Object> response = controller.isUserDisabled(1, "token");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testIsUserDisabledNotFound() throws AuthenticationException {
        when(userService.getBanState(1, "token")).thenThrow(new AuthenticationException("User not found"));
        ResponseEntity<Object> response = controller.isUserDisabled(1, "token");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

