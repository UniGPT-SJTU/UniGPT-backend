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
                        false))
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
                        false))
                .collect(Collectors.toList());

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getCreatedBots(Integer userid, String token, Integer page, Integer pageSize) {
        Optional<User> optionalUser = repository.findById(userid);
        if (optionalUser.isEmpty()) {
            throw new NoSuchElementException("User with id " + userid + " not found");
        }

        List<Bot> createdBots = optionalUser.get().getCreateBots();

        List<BotBriefInfoDTO> bots = createdBots.stream()
                .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                        false))
                .collect(Collectors.toList());

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));

    }

    // type : id || name
    // q: keyword
    public GetUsersOkResponseDTO getUsers(Integer page, Integer pagesize, String token, String type, String q)
            throws AuthenticationException {

        // check if the user is authenticated
        User requestUser = authService.getUserByToken(token);
        if (requestUser == null) {
            throw new AuthenticationException("Unauthorized to get users");
        }
        if (requestUser.isAsAdmin() == false) {
            throw new AuthenticationException("Unauthorized to get users");
        }
        List<User> users = repository.findAll();

        List<UserBriefInfoDTO> userBriefInfoDTOS;
        System.out.println("type: " + type + " q: " + q);
        if (type.equals("id")) {
            Integer id = Integer.parseInt(q);
            userBriefInfoDTOS = users.stream()
                    .filter(user -> user.getId() == id)
                    .map(UserBriefInfoDTO::new)
                    .collect(Collectors.toList());
        } else {
            userBriefInfoDTOS = users.stream()
                    .filter(user -> user.getName().contains(q))
                    .map(UserBriefInfoDTO::new)
                    .collect(Collectors.toList());
        }

        return new GetUsersOkResponseDTO(userBriefInfoDTOS.size(),
                PaginationUtils.paginate(userBriefInfoDTOS, page, pagesize));
    }

    public void setBanUser(Integer id, String token, Boolean state) throws AuthenticationException {
        User requestUser = authService.getUserByToken(token);
        if (requestUser == null) {
            throw new AuthenticationException("Unauthorized to ban user");
        }
        if (requestUser.isAsAdmin() == false) {
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
}
