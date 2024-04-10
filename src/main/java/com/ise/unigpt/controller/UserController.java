package com.ise.unigpt.controller;

import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.dto.UpdateUserInfoResponseDTO;
import com.ise.unigpt.model.User;
import com.ise.unigpt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public Optional<User> getUserProfile(@PathVariable Integer id) {
        return service.findUserById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserInfoResponseDTO> updateUserProfile(@PathVariable Integer id,
                                                                       @RequestBody UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        boolean updateUserProfileResult = service.updateUserInfo(id, updateUserInfoRequestDTO);
        if(updateUserProfileResult) {
            return ResponseEntity.ok(new UpdateUserInfoResponseDTO(true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UpdateUserInfoResponseDTO(false));
        }
    }

}
