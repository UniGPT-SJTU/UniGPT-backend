package com.ise.unigpt;


import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.serviceimpl.ChatHistoryServiceImpl;
import com.ise.unigpt.utils.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.naming.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


public class ChatHistoryServiceTest {

    @Mock
    private BotRepository botRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private PromptChatRepository promptChatRepository;

    @Mock
    private AuthService authService;

    @Mock
    private ChatHistoryService chatHistoryService;

    @InjectMocks
    private ChatHistoryServiceImpl chatHistoryServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDeleteChats() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        List<Chat> chats = new ArrayList<>();
        history.setChats(chats);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(user);
        chatHistoryServiceImpl.deleteChats(1, 1, "token");
        verify(historyRepository, times(1)).findById(1);
        verify(authService, times(1)).getUserByToken("token");
        verify(historyRepository, times(1)).save(history);
    }

    @Test
    void testDeleteChatsUserNotAuthorized() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        List<Chat> chats = new ArrayList<>();
        history.setChats(chats);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        try {
            chatHistoryServiceImpl.deleteChats(1, 1, "token");
        } catch (AuthenticationException e) {
            assertEquals("User not authorized to access this history", e.getMessage());
        }
    }

    @Test
    void testCreateChat() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        List<Chat> chats = new ArrayList<>();
        history.setChats(chats);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(user);
        chatHistoryServiceImpl.createChat(1, "content", ChatType.USER, "token");
        verify(historyRepository, times(1)).findById(1);
        verify(authService, times(1)).getUserByToken("token");
        verify(historyRepository, times(1)).save(history);
    }

    @Test
    void testCreateChatUserNotAuthorized() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        List<Chat> chats = new ArrayList<>();
        history.setChats(chats);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        try {
            chatHistoryServiceImpl.createChat(1, "content", ChatType.USER, "token");
        } catch (AuthenticationException e) {
            assertEquals("User not authorized to access this history", e.getMessage());
        }
    }

    @Test
    void testGetChats() throws Exception {
        List<Chat> chatList = Arrays.asList(
                new Chat(TestHistoryFactory.CreateHistory(), ChatType.USER, "content"),
                new Chat(TestHistoryFactory.CreateHistory(), ChatType.BOT, "content"));
        History history = TestHistoryFactory.CreateHistory();
        history.setChats(chatList);
        User user = TestUserFactory.createUser();
        history.setUser(user);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(user);
        GetChatsOkResponseDTO response = chatHistoryServiceImpl.getChats(1, 0, 20, "token");
        assertEquals(2, response.getTotal());
    }

    @Test
    void testGetChatsUserNotAuthorized() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        List<Chat> chats = new ArrayList<>();
        history.setChats(chats);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        try {
            chatHistoryServiceImpl.getChats(1, 0, 20, "token");
        } catch (AuthenticationException e) {
            assertEquals("User not authorized to access this history", e.getMessage());
        }
    }

    @Test
    void testGetPromptList() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("key1", "value1"),
                new PromptDTO("key2", "value2"));
        history.setPromptKeyValuePairs(promptList.stream().collect(Collectors.toMap(PromptDTO::getKey, PromptDTO::getValue)));
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        List<PromptDTO> response = chatHistoryServiceImpl.getPromptList(1);
        assertEquals(2, response.size());
    }

    @Test
    void testGetPromptListNoSuchElementException() {
        when(historyRepository.findById(1)).thenThrow(new NoSuchElementException("Not found"));
        try {
            chatHistoryServiceImpl.getPromptList(1);
        } catch (NoSuchElementException e) {
            assertEquals("Not found", e.getMessage());
        }
    }

    @Test
    void testGetHistory() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        History response = chatHistoryServiceImpl.getHistory(1);
        assertEquals(history, response);
    }

    @Test
    void testGetHistoryNoSuchElementException() {
        when(historyRepository.findById(1)).thenThrow(new NoSuchElementException("Not found"));
        try {
            chatHistoryServiceImpl.getHistory(1);
        } catch (NoSuchElementException e) {
            assertEquals("Not found", e.getMessage());
        }
    }


    @Test
    void testDeleteHistory() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(user);
        chatHistoryServiceImpl.deleteHistory( "token", 1);
        verify(historyRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteHistoryUserNotAuthorized() throws Exception {
        History history = TestHistoryFactory.CreateHistory();
        User user = TestUserFactory.createUser();
        history.setUser(user);
        when(historyRepository.findById(1)).thenReturn(java.util.Optional.of(history));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        try {
            chatHistoryServiceImpl.deleteHistory("token", 1);
        } catch (AuthenticationException e) {
            assertEquals("unauthorized", e.getMessage());
        }
    }

    @Test
    void testDeleteHistoryUserNoSuchElementException() throws Exception {
        when(historyRepository.findById(1)).thenThrow(new NoSuchElementException("Not found"));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());
        try {
            chatHistoryServiceImpl.deleteHistory("token", 1);
        } catch (NoSuchElementException e) {
            assertEquals("Not found", e.getMessage());
        }
    }
}
