package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

@RestController
@RequestMapping("/api/file")
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final AuthService authService;


    public FileUploadController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseDTO> uploadFile(@CookieValue("token") String token,
                                                  @RequestParam("file") MultipartFile file
    ) {
        try {
            String secondPath = "";
            User user = authService.getUserByToken(token);
            int userId = user.getId();
            secondPath = "user" + userId + "/";

            logger.info("Uploading file: " + file.getOriginalFilename());

            File directory = new File("src/main/resources/static/images/" + secondPath);
            if (!directory.exists()){
                directory.mkdir();
            }

            String filePath = "src/main/resources/static/images/" + secondPath + file.getOriginalFilename();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(filePath));
            stream.write(file.getBytes());
            stream.close();

            String imageUrl = "http://localhost:8080/images/" + secondPath + file.getOriginalFilename();

            // 发送文件到服务器
//            String serverURL = "http://139.224.10.154:10339";
//            URL url = new URL(serverURL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestMethod("POST");

            // 将文件内容发送到服务器
//            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
//            OutputStream outputStream = connection.getOutputStream();
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            outputStream.flush();

//            logger.info("File uploaded successfully");
//
//            // 获取服务器返回的图片URL
//            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            String imageUrl = reader.readLine();
//
//            // 关闭连接和资源
//            reader.close();
//            outputStream.close();
//            inputStream.close();
//            connection.disconnect();

            // 返回图片URL给前端
            return ResponseEntity.ok(new ResponseDTO(true, imageUrl));

        } catch (IOException e) {

            logger.error("Failed to upload file", e);
            return ResponseEntity.badRequest().body(new ResponseDTO(false, "Failed to upload file"));

        }
    }
}
