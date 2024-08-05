package com.ise.unigpt.serviceimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.dto.BotEditInfoDTO;
import com.ise.unigpt.dto.CommentDTO;
import com.ise.unigpt.dto.CreateBotHistoryOkResponseDTO;
import com.ise.unigpt.dto.GetBotHistoryOkResponseDTO;
import com.ise.unigpt.dto.GetBotsOkResponseDTO;
import com.ise.unigpt.dto.GetCommentsOkResponseDTO;
import com.ise.unigpt.dto.PromptDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.model.Comment;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.Memory;
import com.ise.unigpt.model.MemoryItem;
import com.ise.unigpt.model.Plugin;
import com.ise.unigpt.model.PromptChat;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.MemoryRepository;
import com.ise.unigpt.repository.PluginRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.utils.PaginationUtils;
import com.ise.unigpt.utils.StringTemplateParser;

@Service
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final MemoryRepository memoryRepository;
    private final PluginRepository pluginRepository;

    private final PromptChatRepository promptChatRepository;
    private final AuthService authService;

    public BotServiceImpl(BotRepository botRepository,
            UserRepository userRepository,
            HistoryRepository historyRepository,
            MemoryRepository memoryRepository,
            PromptChatRepository promptChatRepository,
            AuthService authService, PluginRepository pluginRepository) {
        this.botRepository = botRepository;
        this.userRepository = userRepository;
        this.promptChatRepository = promptChatRepository;
        this.authService = authService;
        this.historyRepository = historyRepository;
        this.memoryRepository = memoryRepository;
        this.pluginRepository = pluginRepository;
    }

    // TODO: 修改BotBriefInfoDTO.asCreator
    public GetBotsOkResponseDTO getBots(String q, String order, Integer page, Integer pageSize) {
        List<BotBriefInfoDTO> bots;
        if (order.equals("latest")) {
            bots = botRepository.findAllByOrderByIdDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .filter(bot -> bot.getIsPublished())
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                    false, false))
                    .collect(Collectors.toList());
        } else if (order.equals("like")) {
            bots = botRepository.findAllByOrderByLikeNumberDesc()
                    .stream()
                    .filter(bot -> q.isEmpty() || bot.getName().contains(q))
                    .filter(bot -> bot.getIsPublished())
                    .map(bot -> new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                    false, false))
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid order parameter");
        }

        return new GetBotsOkResponseDTO(bots.size(), PaginationUtils.paginate(bots, page, pageSize));
    }

    @Cacheable(value = "botBriefInfo", key = "{#id, #token}")
    public BotBriefInfoDTO getBotBriefInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (!bot.getIsPublished() && bot.getCreator() != user) {
            // 如果bot未发布且请求用户不是bot的创建者，则抛出异常
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }
        return new BotBriefInfoDTO(bot.getId(), bot.getName(), bot.getDescription(), bot.getAvatar(),
                bot.getCreator().equals(user), user.getAsAdmin());
    }

    public BotDetailInfoDTO getBotDetailInfo(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);

        if (!bot.getIsPublished() && bot.getCreator() != user && !user.getAsAdmin()) {
            // 以下三种情况任意一种满足时，可以查看bot的详细信息
            // 1. bot已发布
            // 2. 请求用户是bot的创建者
            // 3. 请求用户是管理员

            // 如果均不是，检查是否是 bot 是否在用户的 usedBots 列表中，如有则删除
            if (user.getUsedBots().contains(bot)) {
                user.getUsedBots().remove(bot);
                userRepository.save(user);
            }

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

        if ((bot.getCreator().getId() != user.getId()) && !user.getAsAdmin()) {
            // 以下两种情况任意一种满足时，可以获取bot的编辑信息
            // 1. 请求用户是bot的创建者
            // 2. 请求用户是管理员
            throw new NoSuchElementException("Bot not published for ID: " + id);
        }

        return new BotEditInfoDTO(bot);
    }

    public ResponseDTO createBot(BotEditInfoDTO dto, String token) throws Exception {
        // 根据token获取用户
        User creatorUser;
        try {
            creatorUser = authService.getUserByToken(token);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User not found");
        }

        int promptChatSize = dto.getPromptChats().size();
        if (promptChatSize < 1) {
            // 提示词模板列表不能为空
            throw new BadRequestException("Prompt chats should not be empty");
        }

        if (dto.getPromptChats().get(promptChatSize - 1).getType() != ChatType.USER) {
            // 最后一个提示词模板应该是用户类型
            throw new BadRequestException("Last prompt chat should be user type");
        }

        // 创建promptChats列表并保存到数据库
        List<PromptChat> promptChats = dto.getPromptChats().stream().map(PromptChat::new).collect(Collectors.toList());
        promptChatRepository.saveAll(promptChats);

        // 创建bot的plugin列表
        // 对于每个plugin, 获取其中的id,使用findById方法获取plugin对象, 如果不存在则抛出异常
        // 如果存在则将plugin对象加入到plugin列表中
        List<Plugin> plugins = dto.getPlugins().stream()
                .map(plugin -> pluginRepository.findById(plugin.getId())
                .orElseThrow(() -> new NoSuchElementException("Plugin not found for ID: " + plugin.getId())))
                .collect(Collectors.toList());

        // 创建bot并保存到数据库
        Bot newBot = new Bot(dto, creatorUser);
        newBot.setPromptChats(promptChats);
        newBot.setPlugins(plugins);
        botRepository.save(newBot);

        // 更新用户的createBots列表
        creatorUser.getCreateBots().add(newBot);
        userRepository.save(creatorUser);

        String botId = String.valueOf(newBot.getId());

        return new ResponseDTO(true, botId);
    }

    @Autowired
    private CacheManager cacheManager;

    public ResponseDTO updateBot(Integer id, BotEditInfoDTO dto, String token) {

        // 根据id获取bot
        Bot updatedBot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        // 根据token获取用户, 并检查用户是否有权限更新bot
        User requestUser = authService.getUserByToken(token);
        if (!Objects.equals(updatedBot.getCreator().getId(), requestUser.getId()) && !requestUser.getAsAdmin()) {
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

        List<Plugin> plugins = dto.getPlugins().stream()
                .map(plugin -> pluginRepository.findById(plugin.getId())
                .orElseThrow(() -> new NoSuchElementException("Plugin not found for ID: " + plugin.getId())))
                .collect(Collectors.toList());

        // 更新bot信息并保存到数据库
        updatedBot.updateInfo(dto);
        updatedBot.setPromptChats(promptChats);
        updatedBot.setPlugins(plugins);
        botRepository.save(updatedBot);

        BotBriefInfoDTO briefInfo = new BotBriefInfoDTO(updatedBot, requestUser);
        String key = String.format("%s,%s", id, token);
        Objects.requireNonNull(cacheManager.getCache("botBriefInfo")).put(key, briefInfo);

        return new ResponseDTO(true, "Bot updated successfully");
    }

    public ResponseDTO likeBot(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));
        User user = authService.getUserByToken(token);
        if (bot.getLikeUsers().contains(user)) {
            return new ResponseDTO(false, "Bot already liked");
        }

        bot.setLikeNumber(bot.getLikeNumber() + 1);
        bot.getLikeUsers().add(user);
        user.getLikeBots().add(bot);

        botRepository.save(bot);
        userRepository.save(user);
        return new ResponseDTO(true, "Bot liked successfully");
    }

    public ResponseDTO dislikeBot(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));
        User user = authService.getUserByToken(token);

        if (!bot.getLikeUsers().contains(user)) {
            return new ResponseDTO(false, "Bot not liked yet");
        }

        bot.setLikeNumber(bot.getLikeNumber() - 1);
        bot.getLikeUsers().remove(user);
        user.getLikeBots().remove(bot);

        botRepository.save(bot);
        userRepository.save(user);
        return new ResponseDTO(true, "Bot disliked successfully");
    }

    public ResponseDTO starBot(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user = authService.getUserByToken(token);
        if (bot.getStarUsers().contains(user)) {
            return new ResponseDTO(false, "Bot already starred");
        }

        bot.setStarNumber(bot.getStarNumber() + 1);
        bot.getStarUsers().add(user);
        user.getStarBots().add(bot);
        botRepository.save(bot);
        userRepository.save(user);
        return new ResponseDTO(true, "Bot starred successfully");
    }

    public ResponseDTO unstarBot(Integer id, String token) {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));
        User user = authService.getUserByToken(token);
        if (!bot.getStarUsers().contains(user)) {
            return new ResponseDTO(false, "Bot not starred yet");
        }

        bot.setStarNumber(bot.getStarNumber() - 1);
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
            throws Exception {
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
            userRepository.save(user);
        } else {
            user.getUsedBots().remove(bot);
            user.getUsedBots().add(bot);
            userRepository.save(user);
        }

        // 將提示词列表转换为 key-value 对
        Map<String, String> promptKeyValuePairs = promptList.stream()
                .collect(Collectors.toMap(PromptDTO::getPromptKey, PromptDTO::getPromptValue));

        // 创建新的对话历史并保存到数据库
        History history = new History(
                user,
                bot,
                promptKeyValuePairs,
                bot.getLlmArgs());
        historyRepository.save(history);

        Memory memory = new Memory(history);
        memoryRepository.save(memory);

        // 將用户填写的表单内容与 bot 的 promptChats 进行模板插值，
        // 并保存到数据库
        List<Chat> interpolatedChats = bot.getPromptChats()
                .stream()
                .map(
                        promptChat -> new Chat(
                                history,
                                promptChat.getType(),
                                StringTemplateParser.interpolate(
                                        promptChat.getContent(),
                                        promptKeyValuePairs),
                                false)) // 前面的提示词用户不可见
                .collect(Collectors.toList());

        // 最后一条 chat 是用户的提问，设置为可见
        interpolatedChats.get(interpolatedChats.size() - 1).setIsVisible(true);

        // 将插值后的结果加入对话历史
        // 并保存到数据库
        history.getChats().addAll(interpolatedChats);
        historyRepository.save(history);

        memory.getMemoryItems().addAll(
                interpolatedChats.stream()
                        // TODO: 有可能的话，标记一下userAsk
                        .limit(interpolatedChats.size() - 1)
                        .map(chat -> new MemoryItem(chat, memory))
                        .collect(Collectors.toList()));
        memoryRepository.save(memory);

        // 将对话历史加入用户的 histories 列表
        // user.getHistories().add(history);
        // userRepository.save(user);
        return new CreateBotHistoryOkResponseDTO(
                true, "Chat history created successfully",
                history.getId(),
                interpolatedChats.get(interpolatedChats.size() - 1).getContent());
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
