package com.ise.unigpt.controller;

import com.ise.unigpt.dto.LoginErrorResponseDTO;
import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.LoginOkResponseDTO;
import com.ise.unigpt.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        try {
            // 更新Cookies
            String token = service.login(loginDTO.getUsername(), loginDTO.getPassword());
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginOkResponseDTO(true, token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginErrorResponseDTO(false, e.getMessage()));
        }
    }
}
