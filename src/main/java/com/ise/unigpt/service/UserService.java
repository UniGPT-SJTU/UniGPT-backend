package com.ise.unigpt.service;

import com.ise.unigpt.dto.UpdateUserInfoRequestDTO;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private final UserRepository repository;


    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> findUserById(Integer id) {
        return repository.findById(id);
    }

    /**
     * @brief 更新用户信息
     * @param id 用户的id
     * @param updateUserInfoRequestDTO 更新用户信息请求的DTO
     * @return 是否更新成功
     */
    public boolean updateUserInfo(Integer id, UpdateUserInfoRequestDTO updateUserInfoRequestDTO) {
        Optional<User> optionalUser = repository.findById(id);
        if(optionalUser.isEmpty()) {
            return false;
        }

        User user = optionalUser.get();
        user.setName(updateUserInfoRequestDTO.getName());
        user.setAvatar(updateUserInfoRequestDTO.getAvatar());
        user.setDescription(updateUserInfoRequestDTO.getDescription());

        repository.save(user);
        return true;
    }


}
