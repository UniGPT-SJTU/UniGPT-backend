package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.dto.UserDTO;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;
import com.ise.unigpt.exception.UserDisabledException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.sasl.AuthenticationException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final AuthService authService;

    public UserController(UserService service, AuthService authService) {
        this.service = service;
        this.authService = authService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserProfile(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new UserDTO(service.findUserById(id)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getMyProfile(@CookieValue(value = "token") String token) {
        System.out.println("token: " + token);
        try {
            return ResponseEntity.ok(new UserDTO(authService.getUserByToken(token)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (UserDisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseDTO(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUserProfile(
            @PathVariable Integer id,
            @CookieValue(value = "token") String token,
            @RequestBody UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
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

    @GetMapping
    public ResponseEntity<Object> getUsers(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer pagesize,
            @RequestParam(defaultValue = "keyword") String type,
            @RequestParam(defaultValue = "", required = false) String q,
            @CookieValue(value = "token") String token) {
        try {
            return ResponseEntity.ok(service.getUsers(page, pagesize, token, type, q));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    // 禁用/解除禁用用户
    @PutMapping("/{id}/ban")
    public ResponseEntity<Object> disableUser(@PathVariable Integer id, @CookieValue(value = "token") String token,
            @RequestParam(defaultValue = "false") Boolean disable) {
        try {
            service.setBanUser(id, token, disable);
            return ResponseEntity.ok(new ResponseDTO(true, "Disable/Undisable user successfully"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}/ban")
    public ResponseEntity<Object> isUserDisabled(@PathVariable Integer id, @CookieValue(value = "token") String token) {
        try {
            Boolean banState = service.getBanState(id, token);
            if (banState == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO(false, "User not found"));
            } else if (banState) {
                return ResponseEntity.ok(new ResponseDTO(true, "true"));
            } else {
                return ResponseEntity.ok(new ResponseDTO(true, "false"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(false, e.getMessage()));
        }
    }
}
