package com.ise.unigpt;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


import javax.naming.AuthenticationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ise.unigpt.controller.AuthController;
import com.ise.unigpt.dto.LoginOkResponseDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

public class AuthControllerTest {
    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testJaccountLogin() {
        // Arrange
        String code = "{\"code\": \"testCode\"}";
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "testToken";

        try {
            when(authService.jaccountLogin("testCode")).thenReturn(token);
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Act
        ResponseEntity<Object> result = controller.jaccountLogin(code, response);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof LoginOkResponseDTO);
        assertEquals(token, ((LoginOkResponseDTO) result.getBody()).getToken());
    }

    @Test
    public void testJaccountLoginWithException() {
        // Arrange
        String code = "{\"code\": \"testCode\"}";
        HttpServletResponse response = mock(HttpServletResponse.class);

        try {
            when(authService.jaccountLogin("testCode")).thenThrow(new AuthenticationException());
        } catch (AuthenticationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Act
        ResponseEntity<Object> result = controller.jaccountLogin(code, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());

    }

    // test logout
    @Test
    public void testLogout() {
        // Arrange
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act
        ResponseEntity<Object> result = controller.logout(response);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody() instanceof ResponseDTO);
        assertTrue(((ResponseDTO) result.getBody()).getOk());
    }

}
