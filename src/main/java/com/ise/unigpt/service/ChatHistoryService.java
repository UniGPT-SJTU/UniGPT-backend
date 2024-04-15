package com.ise.unigpt.service;


import com.ise.unigpt.dto.ChatDTO;
import com.ise.unigpt.dto.GetChatsOkResponseDTO;
import com.ise.unigpt.dto.GetPromptListDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatHistoryService {

    private final ChatRepository chatRepository;

    private final HistoryRepository historyRepository;
    private final AuthService authService;

    public ChatHistoryService(
            ChatRepository chatRepository,
            HistoryRepository historyRepository,
            AuthService authService) {
        this.chatRepository = chatRepository;
        this.historyRepository = historyRepository;
        this.authService = authService;
    }

    /**
     * @brief 在指定历史中加入对话
     * @param historyId 历史id
     * @param content 对话的内容
     * @param type 对话的种类(USER, BOT)
     */
    @Transactional
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
    }

    /**
     * @brief 获取指定历史的所有对话列表
     * @param historyId 历史id
     * @return 对话的列表
     */
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
        return new GetPromptListDTO(history.getPromptList());
    }

    public ResponseDTO changePromptList(Integer historyid, List<String> promptList){
        History history = historyRepository.findById(historyid)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyid));
        List<PromptItem> promptItems = new ArrayList<>();
        for (String prompt : promptList) {
            PromptItem promptItem = new PromptItem();
            promptItem.setContent(prompt);
            promptItems.add(promptItem);
        }
        history.setPromptList(promptItems);
        historyRepository.save(history);
        return new ResponseDTO(true, "Prompt list changed successfully");
    }
}
