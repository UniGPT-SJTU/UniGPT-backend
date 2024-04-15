package com.ise.unigpt.service;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BotService {
    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public BotService(BotRepository botRepository, UserRepository userRepository, AuthService authService) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    public GetBotsOkResponseDTO getBots(String q, String order, Integer page, Integer pageSize) {
        List<BotBriefInfoDTO> bots;
        if(order.equals("latest")) {
            bots = botRepository.findAllByOrderByIdDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getAvatar(), bot.getDescription()))
                    .collect(Collectors.toList());
        }
        else if (order.equals("star")) {
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

    public BotDetailInfoDTO getBotDetailInfo(Integer id) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        if (!bot.isPublished()) {
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotDetailInfoDTO(bot);
    }

    public ResponseDTO createBot(CreateBotRequestDTO dto) {
        try {
            Bot bot = new Bot();
            setBotFromDTO(bot, dto);
            botRepository.save(bot);
            return new ResponseDTO(true, "Bot created successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public ResponseDTO updateBot(Integer id, CreateBotRequestDTO dto) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            setBotFromDTO(bot, dto);
            botRepository.save(bot);
            return new ResponseDTO(true, "Bot updated successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
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

    public ResponseDTO likeBot(Integer id, String token) {
        try{
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setLikeNumber(bot.getLikeNumber() + 1);

            User user = authService.getUserByToken(token);

            bot.getLikeUsers().add(user);
            user.getLikeBots().add(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot liked successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public ResponseDTO dislikeBot(Integer id, String token) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setLikeNumber(bot.getLikeNumber() - 1);

            User user = authService.getUserByToken(token);

            bot.getLikeUsers().remove(user);
            user.getLikeBots().remove(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot disliked successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public ResponseDTO starBot(Integer id, String token) {
        try{
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setStarNumber(bot.getStarNumber() + 1);

            User user = authService.getUserByToken(token);

            bot.getStarUsers().add(user);
            user.getStarBots().add(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot starred successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public ResponseDTO unstarBot(Integer id, String token) {
        try{
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            bot.setStarNumber(bot.getStarNumber() - 1);

            User user = authService.getUserByToken(token);

            bot.getStarUsers().remove(user);
            user.getStarBots().remove(bot);

            botRepository.save(bot);
            userRepository.save(user);
            return new ResponseDTO(true, "Bot unstarred successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public GetBotHistoryOkResponseDTO getBotHistory(Integer id, String token, Integer page,Integer pageSize){
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        // find history by bot and user
        History history = user.getHistories().stream()
                .filter(h -> h.getBot().getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("History not found for bot ID: " + id));
        List<Chat> chats = history.getChats();
        // Sort chats by time
        chats.sort((c1, c2) -> c1.getTime().compareTo(c2.getTime()));
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, chats.size());
        return new GetBotHistoryOkResponseDTO(chats.subList(start, end));
    }

    public ResponseDTO addChatHistory(Integer id, String token, String content) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            User user = authService.getUserByToken(token);
                    // find history by bot and user
            History history = user.getHistories().stream()
                .filter(h -> h.getBot().getId() == id)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("History not found for bot ID: " + id));

            List<Chat> chats = history.getChats();

            Chat chat = new Chat();
            chat.setHistory(history);
            chat.setType(ChatType.USER);
            chat.setTime(new Date());
            chat.setContent(content);

            // TODO: GPT response needed here

            chats.add(chat);
            history.setChats(chats);

            // TODO: Check correctness of variables updating

            return new ResponseDTO(true, "Chat added successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }

    public GetCommentsOkResponseDTO getComments(Integer id, Integer page, Integer pageSize) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        List<CommentDTO> comments = bot.getComments()
                .stream()
                .map(comment -> new CommentDTO(comment))
                .collect(Collectors.toList());

        int start = page * pageSize;
        int end = Math.min(start + pageSize, comments.size());
        return new GetCommentsOkResponseDTO(start < end ? comments.subList(start, end) : new ArrayList<>());
    }

    public ResponseDTO createComment(Integer id, String token, String content) {
        try {
            Bot bot = botRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

            User user = authService.getUserByToken(token);

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            bot.getComments().add(new Comment(content, time, user, bot));
            botRepository.save(bot);
            return new ResponseDTO(true, "Comment created successfully");
        } catch (Exception e) {
            return new ResponseDTO(false, e.getMessage());
        }
    }
}
