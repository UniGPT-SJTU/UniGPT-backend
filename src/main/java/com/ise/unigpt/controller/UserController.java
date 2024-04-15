package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.dto.UserDTO;
import com.ise.unigpt.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserProfile(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new UserDTO(service.findUserById(id)));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUserProfile(
            @PathVariable Integer id,
            @CookieValue(value = "token") String token,
            @RequestBody UpdateUserInfoRequestDTO updateUserInfoRequestDTO
    ) {
        try {
            service.updateUserInfo(id, updateUserInfoRequestDTO, token);
            return ResponseEntity.ok(new ResponseDTO(true, "Update user info successfully"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{userid}/used-bots")
    public ResponseEntity<Object> getUsedBots(
            @PathVariable Integer userid,
            @CookieValue(value = "token", required = false) String token,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            // 使用userid和token
            return ResponseEntity.ok(service.getUsedBots(userid, token, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{userid}/starred-bots")
    public ResponseEntity<Object> getStarredBots(
            @PathVariable Integer userid,
            @CookieValue(value = "token", required = false) String token,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            // 使用userid和token
            return ResponseEntity.ok(service.getStarredBots(userid, token, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{userid}/created-bots")
    public ResponseEntity<Object> getCreatedBots(
            @PathVariable Integer userid,
            @CookieValue(value = "token", required = false) String token,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize) {
        try {
            // 使用userid和token
            return ResponseEntity.ok(service.getCreatedBots(userid, token, page, pagesize));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
