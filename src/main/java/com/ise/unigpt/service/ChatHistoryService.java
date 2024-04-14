package com.ise.unigpt.service;


import com.ise.unigpt.dto.ChatDTO;
import com.ise.unigpt.dto.GetChatsOkResponseDTO;
import com.ise.unigpt.model.Chat;
import com.ise.unigpt.model.History;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChatHistoryService {

    private final ChatRepository chatRepository;

    private final HistoryRepository historyRepository;

    public ChatHistoryService(ChatRepository chatRepository, HistoryRepository historyRepository) {
        this.chatRepository = chatRepository;
        this.historyRepository = historyRepository;
    }

    /**
     * @brief 在指定历史中加入对话
     * @param historyId 历史id
     * @param content 对话的内容
     * @param type 对话的种类(USER, BOT)
     */
    @Transactional
    public void createChat(Integer historyId, String content, ChatType type) {
        Chat chat = new Chat();
        History history = historyRepository.findById(historyId).orElseThrow();

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
    public GetChatsOkResponseDTO getChats(Integer historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));
        List<Chat> chats = history.getChats();
        List<ChatDTO> chatDTOs = new ArrayList<>();
        for(Chat chat : chats) {
            chatDTOs.add(new ChatDTO(chat));
        }
        return new GetChatsOkResponseDTO(chatDTOs);
    }
}
