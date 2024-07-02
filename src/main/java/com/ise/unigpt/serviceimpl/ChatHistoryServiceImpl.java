package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.MemoryRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.utils.PaginationUtils;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final HistoryRepository historyRepository;
    private final AuthService authService;
    private final MemoryRepository memoryRepository;

    public ChatHistoryServiceImpl(
            HistoryRepository historyRepository,
            AuthService authService,
            MemoryRepository memoryRepository) {
        this.historyRepository = historyRepository;
        this.authService = authService;
        this.memoryRepository = memoryRepository;
    }

    @Transactional
    public void deleteChats(Integer historyId, Integer n, String token)
            throws AuthenticationException {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));
        User requestUser = authService.getUserByToken(token);

        if (requestUser.getId() != history.getUser().getId()) {
            throw new AuthenticationException("User not authorized to access this history");
        }

        List<Chat> chats = history.getChats();
        int size = chats.size();
        n = Math.min(n, size);
        
        // 删除末尾的n个chat
        for (int i = 0; i < n; i++) {
            chats.remove(size - 1 - i);
        }
        historyRepository.save(history);

        // TODO: deleteMemoryItem in memory
    }

    public void createChat(Integer historyId, String content, ChatType type, String token)
            throws AuthenticationException {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));

        User requestUser = authService.getUserByToken(token);

        if (!requestUser.getId().equals(history.getUser().getId())) {
            throw new AuthenticationException("User not authorized to access this history");
        }

        Chat chat = new Chat(history, type, content, true);

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
        if (requestUser.getId() != history.getUser().getId()) {
            throw new AuthenticationException("User not authorized to access this history");
        }
        List<ChatDTO> chats = history.getChats()
                .stream()
                .filter(chat -> chat.getIsVisible())
                .map(ChatDTO::new)
                .toList();

        return new GetChatsOkResponseDTO(chats.size(), PaginationUtils.paginate(chats, page, pageSize));
    }

    public List<PromptDTO> getPromptList(Integer historyid) {
        History history = historyRepository
                .findById(historyid)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "History not found for ID: " + historyid));

        List<PromptDTO> promptList = history
                .getPromptKeyValuePairs()
                .entrySet()
                .stream()
                .map(
                        entry -> new PromptDTO(
                                entry.getKey(),
                                entry.getValue()))
                .collect(Collectors.toList());
        return promptList;
    }

    public History getHistory(Integer historyId) {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));
        return history;
    }

    public void deleteHistory(String token, Integer historyId) throws Exception {
        User user;
        try {
            user = authService.getUserByToken(token);
        } catch (Exception e) {
            throw new AuthenticationException("unauthorized");
        }
        History targetHistory = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found"));
        if (!targetHistory.getUser().equals(user)) {
            throw new AuthenticationException("unauthorized");
        }

        // 先删除memory，再删除history
        memoryRepository.deleteById(historyId);
        historyRepository.deleteById(historyId);

    }

    @Override
    public void updateHistoryActiveTime(History history) throws Exception {
        history.setLastActiveTime(new Date());
        historyRepository.save(history);
    }

}
