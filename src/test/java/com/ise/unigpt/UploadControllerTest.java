package com.ise.unigpt;

import com.ise.unigpt.config.AuthConfig;
import com.ise.unigpt.controller.AuthController;
import com.ise.unigpt.controller.FileUploadController;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.User;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.utils.TestUserFactory;
import com.mashape.unirest.http.Unirest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @InjectMocks
    private FileUploadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile() {
        User user = TestUserFactory.createUser();
        Mockito.when(authService.getUserByToken("token")).thenReturn(user);
        Path path = Paths.get("src/test/resources/test.jpg");
//        String name = "OIP.jpg";
        String originalFileName = "test.jpg";
        String contentType = "image/jpeg";
        byte[] content = null;

        try {
            content = java.nio.file.Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        MockMultipartFile file = new MockMultipartFile("file",
                originalFileName, contentType, content);

        try {
            mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                            .file(file)
                            .cookie(new Cookie("token", "testToken"))
                            .accept(MediaType.APPLICATION_JSON))
                            .andExpect(MockMvcResultMatchers.status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
