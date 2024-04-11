package com.ise.unigpt.service;

import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.dto.CreateBotRequestDTO;
import com.ise.unigpt.model.Auth;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.AuthRepository;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BotService {
    @Autowired
    private final BotRepository botRepository;

    @Autowired
    private final AuthRepository authRepository;

    @Autowired
    private final UserRepository userRepository;
    public BotService(BotRepository botRepository, AuthRepository authRepository, UserRepository userRepository) {
        this.botRepository = botRepository;
        this.authRepository = authRepository;
        this.userRepository = userRepository;
    }

    public Optional<BotBriefInfoDTO> getBotBriefInfo(Integer id) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return Optional.empty();
        }

        Bot bot = optionalBot.get();
        BotBriefInfoDTO botBriefInfoDTO = new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription());

        return Optional.of(botBriefInfoDTO);
    }

    public Optional<BotDetailInfoDTO> getBotDetailInfo(Integer id) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return Optional.empty();
        }

        Bot bot = optionalBot.get();
        if (!bot.isPublished()) {
            return Optional.empty();
        }

        BotDetailInfoDTO botDetailInfoDTO = new BotDetailInfoDTO(bot);

        return Optional.of(botDetailInfoDTO);
    }

    public void createBot(CreateBotRequestDTO dto) {
        Bot bot = new Bot();
        setBotFromDTO(bot, dto);
        botRepository.save(bot);
    }

    public void updateBot(Integer id, CreateBotRequestDTO dto) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        setBotFromDTO(bot, dto);
        botRepository.save(bot);
    }

    public void setBotFromDTO(Bot bot, CreateBotRequestDTO dto) {
        bot.setName(dto.getName());
        bot.setAvatar(dto.getAvatar());
        bot.setDescription(dto.getDescription());
        bot.setBaseModelAPI(dto.getBaseModelAPI());
        bot.setPublished(dto.isPublished());
        bot.setPhotos(dto.getPhotos());
        bot.setDetail(dto.getDetail());
        bot.setPrompted(dto.isPrompted());
        bot.setPromptContent(dto.getPromptContent());
    }

    public void likeBot(Integer id, String token) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setLikeNumber(bot.getLikeNumber() + 1);

        Optional<Auth> optionalAuth = authRepository.findByToken(token);
        if(optionalAuth.isEmpty()) {
            return;
        }

        Auth auth = optionalAuth.get();
        User user = auth.getUser();

        bot.getLikeUsers().add(user);
        user.getLikeBots().add(bot);

        botRepository.save(bot);
        userRepository.save(user);
    }

    public void dislikeBot(Integer id, String token) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setLikeNumber(bot.getLikeNumber() - 1);

        Optional<Auth> optionalAuth = authRepository.findByToken(token);
        if(optionalAuth.isEmpty()) {
            return;
        }

        Auth auth = optionalAuth.get();
        User user = auth.getUser();

        bot.getLikeUsers().remove(user);
        user.getLikeBots().remove(bot);

        botRepository.save(bot);
        userRepository.save(user);
    }

    public void starBot(Integer id, String token) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setStarNumber(bot.getStarNumber() + 1);

        Optional<Auth> optionalAuth = authRepository.findByToken(token);
        if(optionalAuth.isEmpty()) {
            return;
        }

        Auth auth = optionalAuth.get();
        User user = auth.getUser();

        bot.getStarUsers().add(user);
        user.getStarBots().add(bot);

        botRepository.save(bot);
        userRepository.save(user);
    }

    public void unstarBot(Integer id, String token) {
        Optional<Bot> optionalBot = botRepository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setStarNumber(bot.getStarNumber() - 1);

        Optional<Auth> optionalAuth = authRepository.findByToken(token);
        if(optionalAuth.isEmpty()) {
            return;
        }

        Auth auth = optionalAuth.get();
        User user = auth.getUser();

        bot.getStarUsers().remove(user);
        user.getStarBots().remove(bot);

        botRepository.save(bot);
        userRepository.save(user);
    }


}
