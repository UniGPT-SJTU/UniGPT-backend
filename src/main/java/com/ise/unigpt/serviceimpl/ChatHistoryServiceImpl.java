package com.ise.unigpt.serviceimpl;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.ChatRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.utils.PaginationUtils;

import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final HistoryRepository historyRepository;
    private final AuthService authService;
    private final ChatRepository chatRepository;

    public ChatHistoryServiceImpl(
            HistoryRepository historyRepository,
            AuthService authService,
            ChatRepository chatRepository) {
        this.historyRepository = historyRepository;
        this.authService = authService;
        this.chatRepository = chatRepository;
    }

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
        if (n > size) {
            n = size;
        }
        for (int i = 0; i < n; i++) {
            Chat chat = chats.remove(size - 1 - i);
            chatRepository.delete(chat);
        }
        historyRepository.save(history);
    }

    public void createChat(Integer historyId, String content, ChatType type, String token)
            throws AuthenticationException {
        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new NoSuchElementException("History not found for ID: " + historyId));

        User requestUser = authService.getUserByToken(token);

        if (requestUser.getId() != history.getUser().getId()) {
            throw new AuthenticationException("User not authorized to access this history");
        }

        Chat chat = new Chat(history, type, content);

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
        List<ChatDTO> chats = history.getChats().stream().map(ChatDTO::new).toList();

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

    // public ResponseDTO updatePromptList(Integer historyid, List<PromptDTO> promptList) throws BadRequestException {
    //     History history = historyRepository
    //             .findById(historyid)
    //             .orElseThrow(
    //                     () -> new NoSuchElementException(
    //                             "History not found for ID: " + historyid));

    //     // 校验promptList与promptKeys的对应关系
    //     int promptListSize = promptList.size();
    //     if (promptListSize != history.getPromptList().size()) {
    //         throw new BadRequestException("Prompt list not match");
    //     }
    //     for (int i = 0; i < promptListSize; ++i) {
    //         // TODO: 使用containsKey 效率较低
    //         if (!history
    //                 .getPromptList()
    //                 .containsKey(promptList.get(i).getPromptKey())) {
    //             throw new BadRequestException("Prompt list not match");
    //         }
    //     }

    //     history.setPromptList(
    //             promptList
    //                     .stream()
    //                     .collect(
    //                             Collectors.toMap(
    //                                     PromptDTO::getPromptKey,
    //                                     PromptDTO::getPromptValue)));
    //     historyRepository.save(history);

    //     return new ResponseDTO(true, "Prompt list changed successfully");
    // }

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

        // 删除关联表中的记录
//        user.getHistories().remove(targetHistory);
        // 删除History对象
        historyRepository.deleteById(historyId);
    }
}
