package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.exception.UserDisabledException;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.repository.HistoryRepository;

import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.utils.PaginationUtils;
import com.ise.unigpt.utils.StringTemplateParser;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BotServiceImpl implements BotService {
    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;

    private final PromptChatRepository promptChatRepository;
    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    public BotServiceImpl(BotRepository botRepository,
            UserRepository userRepository,
            HistoryRepository historyRepository,
            PromptChatRepository promptChatRepository,
            AuthService authService,
            ChatHistoryService chatHistoryService) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
        this.promptChatRepository = promptChatRepository;
        this.authService = authService;
        this.historyRepository = historyRepository;
        this.chatHistoryService = chatHistoryService;
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getBots(String q, String order, Integer page, Integer pageSize) {
        List<BotBriefInfoDTO> bots;
        if (order.equals("latest")) {
            bots = botRepository.findAllByOrderByIdDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .filter(bot -> bot.isPublished())
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                            false, false))
                    .collect(Collectors.toList());
        } else if (order.equals("like")) {
            bots = botRepository.findAllByOrderByLikeNumberDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .filter(bot -> bot.isPublished())
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                            false, false))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid order parameter");
        }

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));
    }

    public BotBriefInfoDTO getBotBriefInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                    false, false);
        } catch (UserDisabledException e) {
            return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                    false, false);
        } catch (Exception e) {
            return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                    false, false);
        }

        if (!bot.isPublished() && bot.getCreator() != user) {
            // 如果bot未发布且请求用户不是bot的创建者，则抛出异常
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }
        return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                bot.getCreator().equals(user), user.isAsAdmin());
    }

    public BotDetailInfoDTO getBotDetailInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            return new BotDetailInfoDTO(bot, null);
        }

        if (!bot.isPublished() && bot.getCreator() != user && !user.isAsAdmin()) {
            // 以下三种情况任意一种满足时，可以查看bot的详细信息
            // 1. bot已发布
            // 2. 请求用户是bot的创建者
            // 3. 请求用户是管理员
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotDetailInfoDTO(bot, user);
    }

    public BotEditInfoDTO getBotEditInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found");
        }

        if ((bot.getCreator().getId() != user.getId()) && !user.isAsAdmin()) {
            // 以下两种情况任意一种满足时，可以获取bot的编辑信息
            // 1. 请求用户是bot的创建者
            // 2. 请求用户是管理员
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotEditInfoDTO(bot);
    }

    public ResponseDTO createBot(BotEditInfoDTO dto, String token) {
        // 根据token获取用户
        User creatorUser;
        try {
            creatorUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found");
        }

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

        String botId = String.valueOf(newBot.getId());

        return new ResponseDTO(true, botId);
    }

    public ResponseDTO updateBot(Integer id, BotEditInfoDTO dto, String token) {

        // 根据id获取bot
        Bot updatedBot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        // 根据token获取用户, 并检查用户是否有权限更新bot
        User requestUser;
        try {
            requestUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found");

        }
        if (updatedBot.getCreator().getId() != requestUser.getId() && !requestUser.isAsAdmin()) {
            // 以下两种情况任意一种满足时，可以更新bot
            // 1. 请求用户是bot的创建者
            // 2. 请求用户是管理员
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
        List<History> historyList = user.getHistories().stream().filter(history -> history.getBot() == bot)
                .collect(Collectors.toList());

        // 按 history 的 chats 中最新一条 chat 的时间倒序排序
        Collections.sort(historyList, Comparator.comparing(History::getLatestChatTime).reversed());

        return new GetBotHistoryOkResponseDTO(historyList.size(),
                PaginationUtils.paginate(historyList, page, pageSize));
    }

    public GetCommentsOkResponseDTO getComments(Integer id, Integer page, Integer pageSize) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        List<CommentDTO> comments = bot.getComments()
                .stream()
                .map(comment -> new CommentDTO(comment))
                .sorted(Comparator.comparing(CommentDTO::getTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return new GetCommentsOkResponseDTO(comments.size(), PaginationUtils.paginate(comments, page, pageSize));
    }

    public CreateBotHistoryOkResponseDTO createBotHistory(Integer id, String token, List<PromptDTO> promptList)
            throws BadRequestException {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        // 校验promptList与bot.promptKeys的对应关系
        int promptListSize = promptList.size();
        if (promptListSize != bot.getPromptKeys().size()) {
            throw new BadRequestException("Prompt list not match");
        }
        for (int i = 0; i < promptListSize; ++i) {
            if (!promptList.get(i).getPromptKey().equals(bot.getPromptKeys().get(i))) {
                throw new BadRequestException("Prompt list not match");
            }
        }

        User user = authService.getUserByToken(token);

        // 将对应 bot 加入用户的 usedBots 列表
        if (!user.getUsedBots().contains(bot)) {
            user.getUsedBots().add(bot);
        }
        userRepository.save(user);

        // 將提示词列表转换为 key-value 对
        Map<String, String> promptKeyValuePairs = promptList.stream()
                .collect(Collectors.toMap(PromptDTO::getPromptKey, PromptDTO::getPromptValue));

        // 將用户填写的表单内容与 bot 的 promptChats 进行模板插值，
        // 并保存到数据库
        List<PromptChat> interpolatedPromptChats = bot.getPromptChats()
                .stream()
                .map(
                        promptChat -> new PromptChat(
                                promptChat.getType(),
                                StringTemplateParser.interpolate(
                                        promptChat.getContent(),
                                        promptKeyValuePairs)))
                .collect(Collectors.toList());

        // 创建新的对话历史
        History history = new History(
                user,
                bot,
                promptKeyValuePairs,
                interpolatedPromptChats);
        historyRepository.save(history);

        // 读取最后一条对话
        PromptChat lastPromptChat = interpolatedPromptChats.get(interpolatedPromptChats.size() - 1);
        String lastPromptChatContent = lastPromptChat.getContent();
        PromptChatType lastPromptChatType = lastPromptChat.getType();
        if (lastPromptChatType != PromptChatType.USER) {
            throw new RuntimeException("Last prompt chat is not of type USER");
        }

        // 删除PromptChatList中的最后一条对话
        interpolatedPromptChats.remove(interpolatedPromptChats.size() - 1);
        promptChatRepository.saveAll(interpolatedPromptChats);

        // 将对话历史加入用户的 histories 列表
        try {
            chatHistoryService.createChat(history.getId(), lastPromptChatContent, ChatType.USER,
                    token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 将对话历史加入用户的 histories 列表
        user.getHistories().add(history);
        userRepository.save(user);
        return new CreateBotHistoryOkResponseDTO(
                true, "Chat history created successfully",
                history.getId(),
                lastPromptChatContent);
    }

    public ResponseDTO createComment(Integer id, String token, String content) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        Comment newComment = new Comment(content, new Date(), user, bot);
        bot.getComments().add(newComment);
        botRepository.save(bot);

        return new ResponseDTO(true, "Comment created successfully");
    }
}
