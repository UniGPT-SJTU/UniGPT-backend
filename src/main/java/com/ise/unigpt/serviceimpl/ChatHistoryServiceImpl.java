package com.ise.unigpt.serviceimpl;


import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.ChatRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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

        Chat chat = new Chat(history, type, content);
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

    public List<PromptDTO> getPromptList(Integer historyid){
        History history = historyRepository
                            .findById(historyid)
                            .orElseThrow(
                                () -> 
                                new NoSuchElementException(
                                    "History not found for ID: " + historyid
                                )
                            );

        List<PromptDTO> promptList = history
                                        .getPromptList()
                                        .entrySet()
                                        .stream()
                                        .map(
                                            entry -> new PromptDTO(
                                                entry.getKey(), 
                                                entry.getValue()
                                            )
                                        )
                                        .collect(Collectors.toList());
        return promptList;
    }

    public ResponseDTO updatePromptList(Integer historyid,  List<PromptDTO> promptList) throws BadRequestException{
        History history = historyRepository
                            .findById(historyid)
                            .orElseThrow(
                                () -> 
                                new NoSuchElementException(
                                    "History not found for ID: " + historyid
                                )
                            );

        // 校验promptList与promptKeys的对应关系
        int promptListSize = promptList.size();
        if(promptListSize != history.getPromptList().size()) {
            throw new BadRequestException("Prompt list not match");
        }
        for(int i = 0;i < promptListSize; ++i) {
            // TODO: 使用containsKey 效率较低
            if(
                !history
                    .getPromptList()
                    .containsKey(promptList.get(i).getPromptKey())
                ) {
                throw new BadRequestException("Prompt list not match");
            }
        }

        history.setPromptList(
            promptList
                .stream()
                .collect(
                    Collectors.toMap(
                        PromptDTO::getPromptKey, 
                        PromptDTO::getPromptValue
                    )
                )
        );
        historyRepository.save(history);

        return new ResponseDTO(true, "Prompt list changed successfully");
    }
}
