package com.ise.unigpt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.dto.BotEditInfoDTO;
import com.ise.unigpt.dto.PromptChatDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.model.BaseModelType;

public class TestBotFactory {
    public static Bot createBot() {
        Bot bot = new Bot();
        bot.setId(1);
        bot.setName("bot1");
        bot.setAvatar("avatar1");
        bot.setDescription("description1");
        bot.setBaseModelAPI(BaseModelType.fromValue(0));
        bot.setPublished(true);
        bot.setDetail("detail1");
        bot.setPhotos(new ArrayList<>(List.of("photo1", "photo2")));
        bot.setPrompted(true);
        bot.setPromptChats(new ArrayList<>(List.of(TestPromptChatFactory.createUserPromptChat(),
                TestPromptChatFactory.createBotPromptChat(), TestPromptChatFactory.createUserPromptChat())));
        bot.setPromptKeys(new ArrayList<>(List.of("prompt1", "prompt2")));
        bot.setLikeNumber(1);
        bot.setStarNumber(1);
        bot.setLikeUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setStarUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setCreator(TestUserFactory.createUser());
        bot.setComments(new ArrayList<>(List.of(TestCommentFactory.createComment(TestUserFactory.createUser(), bot))));
        return bot;
    }

    public static Bot createBot2() {
        Bot bot = new Bot();
        bot.setId(2);
        bot.setName("bot2");
        bot.setAvatar("avatar2");
        bot.setDescription("description2");
        bot.setBaseModelAPI(BaseModelType.fromValue(1));
        bot.setPublished(true);
        bot.setDetail("detail2");
        bot.setPhotos(new ArrayList<>(List.of("photo1", "photo2")));
        bot.setPrompted(true);
        bot.setPromptChats(new ArrayList<>(List.of(TestPromptChatFactory.createUserPromptChat(),
                TestPromptChatFactory.createBotPromptChat(), TestPromptChatFactory.createUserPromptChat())));
        bot.setPromptKeys(new ArrayList<>(List.of("prompt1", "prompt2")));
        bot.setLikeNumber(1);
        bot.setStarNumber(1);
        bot.setLikeUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setStarUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setCreator(TestUserFactory.createUser2());
        bot.setComments(new ArrayList<>(List.of(TestCommentFactory.createComment(TestUserFactory.createUser(), bot))));
        return bot;
    }

    public static Bot createBot3() {
        Bot bot = new Bot();
        bot.setId(3);
        bot.setName("bot3");
        bot.setAvatar("avatar3");
        bot.setDescription("description3");
        bot.setBaseModelAPI(BaseModelType.fromValue(2));
        bot.setPublished(true);
        bot.setDetail("detail3");
        bot.setPhotos(new ArrayList<>(List.of("photo1", "photo2")));
        bot.setPrompted(true);
        bot.setPromptChats(new ArrayList<>(List.of(TestPromptChatFactory.createUserPromptChat(),
                TestPromptChatFactory.createBotPromptChat(), TestPromptChatFactory.createUserPromptChat())));
        bot.setPromptKeys(new ArrayList<>(List.of("prompt1", "prompt2")));
        bot.setLikeNumber(1);
        bot.setStarNumber(1);
        bot.setLikeUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setStarUsers(new ArrayList<>(List.of(TestUserFactory.createUser())));
        bot.setCreator(TestUserFactory.createUser3());
        bot.setComments(new ArrayList<>(List.of(TestCommentFactory.createComment(TestUserFactory.createUser(), bot))));
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

    public static BotBriefInfoDTO createBotBriefInfoDTO_asCreator() {
        return new BotBriefInfoDTO(1, "bot1", "description1", "avatar1", true, false);
    }

    public static BotDetailInfoDTO createBotDetailInfoDTO() {
        BotDetailInfoDTO dto = new BotDetailInfoDTO();
        dto.setId(1);
        dto.setName("bot1");
        dto.setCreator("user1");
        dto.setCreatorId(1);
        dto.setDescription("description1");
        dto.setPhotos(Arrays.asList("photo1", "photo2"));
        dto.setDetail("detail1");
        dto.setAvatar("avatar1");
        dto.setBaseModelAPI("baseModelAPI1");
        dto.setLikeNumber(1);
        dto.setStarNumber(1);
        dto.setLiked(false);
        dto.setStarred(false);
        dto.setAsCreator(true);
        dto.setAsAdmin(false);
        dto.setPromptKeys(Arrays.asList("prompt1", "prompt2"));
        dto.setLiked(true);
        dto.setStarred(true);
        return dto;
    }

    public static BotEditInfoDTO createBotEditInfoDTO() {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        dto.setName("bot1");
        dto.setAvatar("avatar1");
        dto.setDescription("description1");
        dto.setBaseModelAPI(0);
        dto.setPublished(true);
        dto.setDetail("detail1");
        dto.setPhotos(Arrays.asList("photo1", "photo2"));
        dto.setPrompted(true);
        PromptChatDTO userPromptChat = new PromptChatDTO(TestPromptChatFactory.createUserPromptChat());
        PromptChatDTO botPromptChat = new PromptChatDTO(TestPromptChatFactory.createBotPromptChat());
        dto.setPromptChats(Arrays.asList(userPromptChat, botPromptChat, userPromptChat));
        dto.setPromptKeys(Arrays.asList("prompt1", "prompt2"));
        return dto;
    }
}