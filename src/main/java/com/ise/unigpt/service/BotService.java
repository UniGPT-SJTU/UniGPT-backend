package com.ise.unigpt.service;

import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.dto.CreateBotRequestDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.repository.BotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BotService {
    @Autowired
    private final BotRepository repository;

    public BotService(BotRepository repository) {
        this.repository = repository;
    }

    public Optional<BotBriefInfoDTO> getBotBriefInfo(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return Optional.empty();
        }

        Bot bot = optionalBot.get();
        BotBriefInfoDTO botBriefInfoDTO = new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription());

        return Optional.of(botBriefInfoDTO);
    }

    public Optional<BotDetailInfoDTO> getBotDetailInfo(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return Optional.empty();
        }

        Bot bot = optionalBot.get();
        BotDetailInfoDTO botDetailInfoDTO = new BotDetailInfoDTO(bot);

        return Optional.of(botDetailInfoDTO);
    }

    public void createBot(CreateBotRequestDTO dto) {
        Bot bot = new Bot();
        setBotFromDTO(bot, dto);
        repository.save(bot);
    }

    public void updateBot(Integer id, CreateBotRequestDTO dto) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        setBotFromDTO(bot, dto);
        repository.save(bot);
    }

    public void setBotFromDTO(Bot bot, CreateBotRequestDTO dto) {
        bot.setName(dto.getName());
        bot.setAvatar(dto.getAvatar());
        bot.setDescription(dto.getDescription());
        bot.setBaseModelAPI(dto.getBaseModelAPI());
        bot.setPublished(dto.isPublished());
        bot.setDetail(dto.getDetail());
        bot.setPrompted(dto.isPrompted());
        bot.setPromptContent(dto.getPromptContent());
    }

    public void likeBot(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setLikeNumber(bot.getLikeNumber() + 1);
        repository.save(bot);
    }

    public void dislikeBot(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setLikeNumber(bot.getLikeNumber() - 1);
        repository.save(bot);
    }

    public void starBot(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setStarNumber(bot.getStarNumber() + 1);
        repository.save(bot);
    }

    public void unstarBot(Integer id) {
        Optional<Bot> optionalBot = repository.findById(id);
        if(optionalBot.isEmpty()) {
            return;
        }

        Bot bot = optionalBot.get();
        bot.setStarNumber(bot.getStarNumber() - 1);
        repository.save(bot);
    }


}
