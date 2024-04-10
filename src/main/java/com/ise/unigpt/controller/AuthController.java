package com.ise.unigpt.controller;

import com.ise.unigpt.dto.LoginFailureResponseDTO;
import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.LoginSuccessResponseDTO;
import com.ise.unigpt.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        Optional<String> optionalToken = service.login(loginDTO.getUsername(), loginDTO.getPassword());
        if(optionalToken.isPresent()) {

            Cookie cookie = new Cookie("token", optionalToken.get());
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginSuccessResponseDTO(true, optionalToken.get()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginFailureResponseDTO(false, "Login fails!"));
        }
    }
}
