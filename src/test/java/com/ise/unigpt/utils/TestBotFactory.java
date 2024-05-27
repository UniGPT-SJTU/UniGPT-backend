package com.ise.unigpt.utils;

import java.util.Arrays;

import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.model.Bot;

public class TestBotFactory {
    public static Bot createBot() {
        Bot bot = new Bot();
        bot.setId(1);
        bot.setName("bot1");
        bot.setCreator(TestUserFactory.createUser());
        bot.setDescription("description1");
        bot.setPhotos(Arrays.asList("photo1", "photo2"));
        bot.setDetail("detail1");
        bot.setAvatar("avatar1");
        bot.setBaseModelAPI("baseModelAPI1");
        bot.setLikeNumber(0);
        bot.setStarNumber(0);
        bot.setPromptKeys(Arrays.asList("prompt1", "prompt2"));
        return bot;
    }
    public static BotBriefInfoDTO createBotBriefInfoDTO() {
        return new BotBriefInfoDTO(1, "bot1", "description1", "avatar1", false, false);
    }
    public static BotBriefInfoDTO createBotBriefInfoDTO2() {
        return new BotBriefInfoDTO(2, "bot2", "description2", "avatar2", false, false);
    }
    public static BotBriefInfoDTO createBotBriefInfoDTO3() {
        return new BotBriefInfoDTO(3, "bot3", "description3", "avatar3", false, false);
    }



    public static BotDetailInfoDTO createBotDetailInfoDTO() {
        BotDetailInfoDTO dto = new BotDetailInfoDTO();
        dto.setId(1);
        dto.setName("bot1");
        dto.setCreator("creator1");
        dto.setCreatorId(1);
        dto.setDescription("description1");
        dto.setPhotos(Arrays.asList("photo1", "photo2"));
        dto.setDetail("detail1");
        dto.setAvatar("avatar1");
        dto.setBaseModelAPI("baseModelAPI1");
        dto.setLikeNumber(0);
        dto.setStarNumber(0);
        dto.setLiked(false);
        dto.setStarred(false);
        dto.setAsCreator(false);
        dto.setAsAdmin(false);
        dto.setPromptKeys(Arrays.asList("prompt1", "prompt2"));
        return dto;
    }
}