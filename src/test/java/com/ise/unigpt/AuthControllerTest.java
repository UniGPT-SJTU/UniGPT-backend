package com.ise.unigpt;

import com.google.gson.Gson;
import com.ise.unigpt.controller.AuthController;
import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.naming.AuthenticationException;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new Gson();
    }

    @Test
    void shouldRegisterOk() throws Exception {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "username",
                "email",
                "password",
                "avatar",
                "description"
        );
        Mockito.doNothing().when(authService).register(Mockito.any(RegisterRequestDTO.class));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(registerRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok", is(true)));
    }

    @Test
    void shouldLoginOk() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("username");
        loginRequestDTO.setPassword("password");

        String token = "abc-123";
        Mockito.when(authService.login(Mockito.any(LoginRequestDTO.class))).thenReturn(token);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok", is(true)))
                .andExpect(jsonPath("$.token", is(token)))
                .andExpect(cookie().exists("token"))
                .andExpect(cookie().value("token", token));
    }

    @Test
    void shouldLoginErrorWhenPasswordIsWrong() throws Exception {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("username");
        loginRequestDTO.setPassword("wrong password");

        Mockito.when(authService.login(loginRequestDTO)).thenThrow(new AuthenticationException("Invalid username or password"));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(loginRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.ok", is(false)))
                .andExpect(jsonPath("$.message",is("Invalid username or password")))
                .andExpect(cookie().doesNotExist("token"));
    }

    @Test
    void shouldLogoutOk() throws Exception {
        // TODO: 初始化Cookies
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok", is(true)))
                .andExpect(cookie().value("token", ""));
    }
}
