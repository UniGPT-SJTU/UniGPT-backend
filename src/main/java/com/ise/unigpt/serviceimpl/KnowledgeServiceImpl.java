package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.Bot;
import com.ise.unigpt.model.User;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.service.KnowledgeService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;

@Service
public class KnowledgeServiceImpl implements KnowledgeService{
    private final BotRepository botRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final PromptChatRepository promptChatRepository;
    private final AuthService authService;
    private final ChatHistoryService chatHistoryService;

    public KnowledgeServiceImpl(BotRepository botRepository,
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

    public ResponseDTO uploadFile(Integer id, String token, MultipartFile file) throws AuthenticationException {
        Bot bot = botRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Bot not found for ID: " + id));

        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (NoSuchElementException e) { throw new NoSuchElementException("User not found");}

        if(!bot.getCreator().equals(user) && !user.getAsAdmin()){
            throw new AuthenticationException("Unauthorized to upload file.");
        }



        return new ResponseDTO(true, "Successfully upload " + file.getOriginalFilename());
    }
}
