package com.ise.unigpt.controller;

import com.ise.unigpt.dto.ErrorResponseDTO;
import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.dto.UpdateUserInfoResponseDTO;
import com.ise.unigpt.dto.UserDTO;
import com.ise.unigpt.model.User;
import com.ise.unigpt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
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
                    .body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserInfoResponseDTO> updateUserProfile(@PathVariable Integer id,
                                                                       @RequestBody UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        try {
            service.updateUserInfo(id, updateUserInfoRequestDTO);
            return ResponseEntity.ok(new UpdateUserInfoResponseDTO(true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UpdateUserInfoResponseDTO(false));
        }
    }

}
