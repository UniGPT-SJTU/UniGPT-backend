package com.ise.unigpt.controller;


import com.ise.unigpt.dto.LoginOkResponseDTO;
import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.utils.CookieUtils;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

import org.json.JSONObject;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        try {
            // 更新Cookies
            String token = service.login(loginDTO);
            CookieUtils.set(response, "token", token, 24 * 60 * 60);
            return ResponseEntity.ok(new LoginOkResponseDTO(true, token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequestDTO registerDTO) {
        try {
            service.register(registerDTO);
            return ResponseEntity.ok(new ResponseDTO(true, "register success"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        CookieUtils.set(response, "token", "", 0);
        return ResponseEntity.ok(new ResponseDTO(true, ""));
    }

    @PostMapping("/jaccountLogin")
    public ResponseEntity<Object> jaccountLogin(@RequestBody String jsonString, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject(jsonString);
        String code = jsonObject.getString("code");
        System.out.println(code);

        try {
            // 更新Cookies
            String token = service.jaccountLogin(code);
            CookieUtils.set(response, "token", token, 24 * 60 * 60);
            return ResponseEntity.ok(new LoginOkResponseDTO(true, token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
