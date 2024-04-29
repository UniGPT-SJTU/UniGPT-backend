package com.ise.unigpt.serviceimpl;


import com.ise.unigpt.dto.ChatDTO;
import com.ise.unigpt.dto.GetChatsOkResponseDTO;
import com.ise.unigpt.dto.GetPromptListDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.ChatRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatRepository chatRepository;

    private final HistoryRepository historyRepository;
    private final AuthService authService;

    public ChatHistoryServiceImpl(
            ChatRepository chatRepository,
            HistoryRepository historyRepository,
            AuthService authService) {
        this.chatRepository = chatRepository;
        this.historyRepository = historyRepository;
        this.authService = authService;
    }

    public void createChat(Integer historyId, String content, ChatType type, String token) throws AuthenticationException {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));

        User requestUser = authService.getUserByToken(token);
        if(!requestUser.equals(history.getUser())) {
            throw new AuthenticationException("User not authorized to access this history");
        }

        Chat chat = new Chat();
        chat.setHistory(history);
        chat.setContent(content);
        chat.setType(type);
        chat.setTime(new Date());

        chatRepository.save(chat);

        history.getChats().add(chat);
        historyRepository.save(history);
    }

    public GetChatsOkResponseDTO getChats(
            Integer historyId,
            Integer page,
            Integer pageSize,
            String token) throws AuthenticationException {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));

        User requestUser = authService.getUserByToken(token);
        if(!requestUser.equals(history.getUser())) {
            throw new AuthenticationException("User not authorized to access this history");
        }
        List<ChatDTO> chats = history.getChats().stream().map(ChatDTO::new).toList();

        int start = page * pageSize;
        int end = Math.min(start + pageSize, chats.size());
        return new GetChatsOkResponseDTO(start < end ? chats.subList(start, end) : new ArrayList<>());
    }

    public GetPromptListDTO getPromptList(Integer historyid){
        History history = historyRepository.findById(historyid)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyid));
        if(history.getPromptValues() == null)
            history.setPromptValues(new ArrayList<>());
        Bot bot = history.getBot();
        if(bot == null)
            throw new NoSuchElementException("Bot not found for history ID: " + historyid);
        if(bot.getPromptKeys() == null)
            bot.setPromptKeys(new ArrayList<>());
        return new GetPromptListDTO(bot.getPromptKeys(), history.getPromptValues());
    }

    public ResponseDTO changePromptList(Integer historyid, List<String> promptList){
        History history = historyRepository.findById(historyid)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyid));
        List<PromptValue> promptValues = new ArrayList<>();
        for(String content: promptList){
            PromptValue promptValue = new PromptValue();
            promptValue.setHistory(history);
            promptValue.setContent(content);
            promptValues.add(promptValue);
        }
        history.setPromptValues(promptValues);
        historyRepository.save(history);
        return new ResponseDTO(true, "Prompt list changed successfully");
    }
}
