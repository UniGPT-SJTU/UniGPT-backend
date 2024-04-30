package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.repository.HistoryRepository;

import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.BotService;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BotServiceImpl implements BotService {
    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    private final PromptChatRepository promptChatRepository;
    private final AuthService authService;

    public BotServiceImpl(BotRepository botRepository,
                          UserRepository userRepository,
                            HistoryRepository historyRepository,
                          PromptChatRepository promptChatRepository,
                          AuthService authService) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
        this.promptChatRepository = promptChatRepository;
        this.authService = authService;
        this.historyRepository = historyRepository;
    }

    public GetBotsOkResponseDTO getBots(String q, String order, Integer page, Integer pageSize) {
        List<BotBriefInfoDTO> bots;
        if (order.equals("latest")) {
            bots = botRepository.findAllByOrderByIdDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription()))
                    .collect(Collectors.toList());
        } else if (order.equals("star")) {
            bots = botRepository.findAllByOrderByStarNumberDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription()))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid order parameter");
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, bots.size());
        return new GetBotsOkResponseDTO(start < end ? bots.subList(start, end) : new ArrayList<>());
    }

    public BotBriefInfoDTO getBotBriefInfo(Integer id) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription());
    }

    public BotDetailInfoDTO getBotDetailInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (!bot.isPublished() && bot.getCreator() != user) {
            // 如果bot未发布且请求用户不是bot的创建者，则抛出异常
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotDetailInfoDTO(bot, user);
    }

    public BotEditInfoDTO getBotEditInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (bot.getCreator().getId() != user.getId()) {
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotEditInfoDTO(bot);
    }

    public ResponseDTO createBot(BotEditInfoDTO dto, String token) {
        // 根据token获取用户
        User creatorUser = authService.getUserByToken(token);

        // 创建promptChats列表并保存到数据库
        List<PromptChat> promptChats = dto.getPromptChats().stream().map(PromptChat::new).collect(Collectors.toList());
        promptChatRepository.saveAll(promptChats);

        // 创建bot并保存到数据库
        Bot newBot = new Bot(dto, creatorUser);
        newBot.setPromptChats(promptChats);
        botRepository.save(newBot);

        // 更新用户的createBots列表
        creatorUser.getCreateBots().add(newBot);
        userRepository.save(creatorUser);

        return new ResponseDTO(true, "Bot created successfully");
    }

    public ResponseDTO updateBot(Integer id, BotEditInfoDTO dto, String token) {

        // 根据id获取bot
        Bot updatedBot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        // 根据token获取用户, 并检查用户是否有权限更新bot
        User requestUser = authService.getUserByToken(token);
        if (updatedBot.getCreator().getId() != requestUser.getId()) {
            throw new IllegalArgumentException("User not authorized to update bot");
        }

        // 删除原有的promptChats列表
        List<PromptChat> oldPromptChats = new ArrayList<>(updatedBot.getPromptChats());
        updatedBot.getPromptChats().clear();
        promptChatRepository.deleteAll(oldPromptChats);

        // 创建promptChats列表并保存到数据库
        List<PromptChat> promptChats = dto.getPromptChats().stream().map(PromptChat::new).collect(Collectors.toList());
        promptChatRepository.saveAll(promptChats);

        // 更新bot信息并保存到数据库
        updatedBot.updateInfo(dto);
        updatedBot.setPromptChats(promptChats);
        botRepository.save(updatedBot);

        return new ResponseDTO(true, "Bot updated successfully");
    }

    public ResponseDTO likeBot(Integer id, String token) {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setLikeNumber(bot.getLikeNumber() + 1);

            User user = authService.getUserByToken(token);

            if (bot.getLikeUsers().contains(user)) {
                return new ResponseDTO(false, "Bot already liked");
            }

            bot.getLikeUsers().add(user);
            user.getLikeBots().add(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot liked successfully");
    }


    public ResponseDTO dislikeBot(Integer id, String token) {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setLikeNumber(bot.getLikeNumber() - 1);

            User user = authService.getUserByToken(token);

            if (!bot.getLikeUsers().contains(user)) {
                return new ResponseDTO(false, "Bot not liked yet");
            }

            bot.getLikeUsers().remove(user);
            user.getLikeBots().remove(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot disliked successfully");
    }

    public ResponseDTO starBot(Integer id, String token) {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setStarNumber(bot.getStarNumber() + 1);

            User user = authService.getUserByToken(token);

            if (bot.getStarUsers().contains(user)) {
                return new ResponseDTO(false, "Bot already starred");
            }

            bot.getStarUsers().add(user);
            user.getStarBots().add(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot starred successfully");
    }


    public ResponseDTO unstarBot(Integer id, String token) {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setStarNumber(bot.getStarNumber() - 1);

            User user = authService.getUserByToken(token);

            if (!bot.getStarUsers().contains(user)) {
                return new ResponseDTO(false, "Bot not starred yet");
            }

            bot.getStarUsers().remove(user);
            user.getStarBots().remove(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot unstarred successfully");

    }

    public GetBotHistoryOkResponseDTO getBotHistory(Integer id, String token, Integer page, Integer pageSize) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);
        List<History> historyList = user.getHistories().stream().filter(history -> history.getBot() == bot).collect(Collectors.toList());
        int start = page * pageSize;
        int end = Math.min(start + pageSize, historyList.size());
        return new GetBotHistoryOkResponseDTO(start < end ? historyList.subList(start, end): new ArrayList<>());
    }

    public GetCommentsOkResponseDTO getComments(Integer id, Integer page, Integer pageSize) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        List<CommentDTO> comments = bot.getComments()
                .stream()
                .map(comment -> new CommentDTO(comment))
                .collect(Collectors.toList());

        System.out.println("Number of comments: " + comments.size());
        System.out.println("Page size: " + pageSize);
        int start = page * pageSize;
        int end = Math.min(start + pageSize, comments.size());

        System.out.println("Start index: " + start);
        System.out.println("End index: " + end);

        return new GetCommentsOkResponseDTO(start < end ? comments.subList(start, end) : new ArrayList<>());
    }

    public ResponseDTO createChatHistory(Integer id, String token, List<PromptDTO> promptList) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            User user = authService.getUserByToken(token);

            History history = new History(user, bot, new ArrayList<>());
            history.setPromptValues(promptList.stream().map(
                    promptDTO -> new PromptValue(history, promptDTO.getPromptValue())).collect(Collectors.toList()));
            historyRepository.save(history);

            user.getHistories().add(history);
            userRepository.save(user);
            return new ResponseDTO(true, "Chat history created successfully");
    } catch (Exception e) {
        return new ResponseDTO(false, e.getMessage());
    }
}

    public ResponseDTO createComment(Integer id, String token, String content) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            User user = authService.getUserByToken(token);

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            Comment newComment = new Comment(content, time, user, bot);
            bot.getComments().add(newComment);
            botRepository.save(bot);

            return new ResponseDTO(true, "Comment created successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }
}
