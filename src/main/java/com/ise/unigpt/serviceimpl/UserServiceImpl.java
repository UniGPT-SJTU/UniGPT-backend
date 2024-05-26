package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.model.User;
import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.UserService;
import com.ise.unigpt.utils.PaginationUtils;

import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AuthService authService;

    public UserServiceImpl(UserRepository repository, AuthService authService) {
        this.repository = repository;
        this.authService = authService;
    }

    public User findUserById(Integer id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User not found for ID: " + id);
        }

        return optionalUser.get();
    }

    public void updateUserInfo(
            Integer id,
            UpdateUserInfoRequestDTO updateUserInfoRequestDTO,
            String token) throws AuthenticationException {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User not found for ID: " + id);
        }

        User targetUser = optionalUser.get();
        User requestUser = authService.getUserByToken(token);
        if (!targetUser.equals(requestUser)) {
            throw new AuthenticationException("Unauthorized to update user info");
        }
        targetUser.setName(updateUserInfoRequestDTO.getName());
        targetUser.setAvatar(updateUserInfoRequestDTO.getAvatar());
        targetUser.setDescription(updateUserInfoRequestDTO.getDescription());
        targetUser.setCanvasUrl(updateUserInfoRequestDTO.getCanvasUrl());

        repository.save(targetUser);
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getUsedBots(Integer userid, String token, Integer page, Integer pageSize)
            throws AuthenticationException {
        Optional<User> optionalUser = repository.findById(userid);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + userid + " not found");
        }

        if (!authService.getUserByToken(token).equals(optionalUser.get())) {
            throw new AuthenticationException("Unauthorized to get used bots");
        }

        List<Bot> usedBots = optionalUser.get().getUsedBots();

        List<BotBriefInfoDTO> bots = usedBots.stream()
                .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                        bot.getCreator().equals(optionalUser.get()), optionalUser.get().isAsAdmin()))
                .collect(Collectors.toList());

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getStarredBots(Integer userid, String token, Integer page, Integer pageSize)
            throws AuthenticationException {
        Optional<User> optionalUser = repository.findById(userid);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + userid + " not found");
        }

        if (!authService.getUserByToken(token).equals(optionalUser.get())) {
            throw new AuthenticationException("Unauthorized to get used bots");
        }

        List<Bot> starredBots = optionalUser.get().getStarBots();

        List<BotBriefInfoDTO> bots = starredBots.stream()
                .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                        bot.getCreator().equals(optionalUser.get()), optionalUser.get().isAsAdmin()))
                .collect(Collectors.toList());

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getCreatedBots(Integer userid, String token, Integer page, Integer pageSize) {
        Optional<User> optionalUser = repository.findById(userid);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + userid + " not found");
        }
        User user = optionalUser.get();
        List<Bot> createdBots = optionalUser.get().getCreateBots();

        List<BotBriefInfoDTO> bots = createdBots.stream()
                .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                        bot.getCreator().equals(user), user.isAsAdmin()))
                .collect(Collectors.toList());

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));

    }

    // type : id || name
    // q: keyword
    public GetUsersOkResponseDTO getUsers(Integer page, Integer pagesize, String token, String type, String q)
            throws AuthenticationException {
        User requestUser;
        try {
            requestUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new AuthenticationException("Unauthorized to get users");
        }

        if (!requestUser.isAsAdmin()) {
            throw new AuthenticationException("Unauthorized to get users");
        }

        List<User> users = repository.findAll();
        List<UserBriefInfoDTO> userBriefInfoDTOs;
        System.out.println("type: " + type + " q: " + q);
        if (type.equals("id")) {
            Integer id;
            try {
                id = Integer.parseInt(q);
            } catch (NumberFormatException e) {
                userBriefInfoDTOs = users.stream()
                        .map(UserBriefInfoDTO::new)
                        .collect(Collectors.toList());
                return new GetUsersOkResponseDTO(userBriefInfoDTOs.size(),
                        PaginationUtils.paginate(userBriefInfoDTOs, page, pagesize));
            }
            userBriefInfoDTOs = users.stream()
                    .filter(user -> user.getId() == id)
                    .map(UserBriefInfoDTO::new)
                    .collect(Collectors.toList());
        } else {
            userBriefInfoDTOs = users.stream()
                    .filter(user -> user.getName().contains(q))
                    .map(UserBriefInfoDTO::new)
                    .collect(Collectors.toList());
        }

        return new GetUsersOkResponseDTO(userBriefInfoDTOs.size(),
                PaginationUtils.paginate(userBriefInfoDTOs, page, pagesize));
    }

    public void setBanUser(Integer id, String token, Boolean state) throws AuthenticationException {
        User requestUser;
        try {
            requestUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new AuthenticationException("Unauthorized to ban user");
        }

        if (!requestUser.isAsAdmin()) {
            throw new AuthenticationException("Unauthorized to ban user");
        }
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        User targetUser = optionalUser.get();
        targetUser.setDisabled(state);
        repository.save(targetUser);
    }

    public Boolean getBanState(Integer id, String token) throws AuthenticationException {
        User requestUser;
        try {
            requestUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new AuthenticationException("Unauthorized to get ban state");
        }
        if (!requestUser.isAsAdmin()) {
            throw new AuthenticationException("Unauthorized to get ban state");
        }

        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + id + " not found");
        }
        return optionalUser.get().isDisabled();
    }
}
