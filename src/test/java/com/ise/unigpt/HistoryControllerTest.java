package com.ise.unigpt;

import com.ise.unigpt.controller.HistoryController;
import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.ChatType;
import com.ise.unigpt.service.ChatHistoryService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.naming.AuthenticationException;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class HistoryControllerTest {

    @Mock
    private ChatHistoryService service;

    @InjectMocks
    private HistoryController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChats_Success() throws Exception {
        Integer id = 1;
        Integer page = 0;
        Integer pageSize = 100;
        String token = "test-token";
        List<ChatDTO> chats = List.of(
                new ChatDTO(
                        1,
                        "Test content",
                        new Date(),
                        "Test avatar",
                        "Test name",
                        ChatType.USER));
        GetChatsOkResponseDTO responseDTO = new GetChatsOkResponseDTO(pageSize, chats);
        when(service.getChats(id, page, pageSize, token)).thenReturn(responseDTO);

        ResponseEntity<Object> response = controller.getChats(id, page, pageSize, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetChats_NotFound() throws Exception {
        Integer id = 1;
        Integer page = 0;
        Integer pageSize = 100;
        String token = "test-token";
        when(service.getChats(id, page, pageSize, token))
                .thenThrow(new NoSuchElementException("Chat history not found"));

        // Act
        ResponseEntity<Object> response = controller.getChats(id, page, pageSize, token);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Chat history not found", ((GetChatsErrorResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetChats_Unauthorized() throws Exception {
        // Arrange
        Integer id = 1;
        Integer page = 0;
        Integer pageSize = 100;
        String token = "test-token";
        when(service.getChats(id, page, pageSize, token)).thenThrow(new AuthenticationException("Unauthorized"));

        // Act
        ResponseEntity<Object> response = controller.getChats(id, page, pageSize, token);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", ((GetChatsErrorResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateChat_Success() throws Exception {
        Integer historyid = 1;
        String token = "test-token";
        CreateChatRequestDTO dto = new CreateChatRequestDTO();
        dto.setContent("Test content");

        ResponseEntity<ResponseDTO> response = controller.createChat(historyid, token, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Chat created", response.getBody().getMessage());

        verify(service, times(1)).createChat(historyid, dto.getContent(), ChatType.USER, token);
    }

    @Test
    void testCreateChat_NotFound() throws Exception {
        Integer historyid = 1;
        String token = "test-token";
        CreateChatRequestDTO dto = new CreateChatRequestDTO();
        dto.setContent("Test content");
        doThrow(new NoSuchElementException("History not found")).when(service).createChat(historyid, dto.getContent(),
                ChatType.USER, token);

        ResponseEntity<ResponseDTO> response = controller.createChat(historyid, token, dto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("History not found", response.getBody().getMessage());
    }

    @Test
    void testCreateChat_unauthorized() throws Exception {
        Integer historyid = 1;
        String token = "test-token";
        CreateChatRequestDTO dto = new CreateChatRequestDTO();
        dto.setContent("Test content");
        doThrow(new AuthenticationException("Unauthorized")).when(service).createChat(historyid, dto.getContent(),
                ChatType.USER, token);

        ResponseEntity<ResponseDTO> response = controller.createChat(historyid, token, dto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", response.getBody().getMessage());
    }

    @Test
    void testGetPromptList_Success() throws Exception {
        Integer historyid = 1;
        List<PromptDTO> responseDTO = List.of(new PromptDTO("prompt key", "Test prompt"));
        when(service.getPromptList(historyid)).thenReturn(responseDTO);

        ResponseEntity<Object> response = controller.getPromptList(historyid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testGetPromptList_NotFound() throws Exception {
        // Arrange
        Integer historyid = 1;
        when(service.getPromptList(historyid)).thenThrow(new NoSuchElementException("Prompt list not found"));

        // Act
        ResponseEntity<Object> response = controller.getPromptList(historyid);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Prompt list not found", ((GetChatsErrorResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testDeleteHistory_Success() throws Exception {
        String token = "test-token";
        Integer historyid = 1;

        ResponseEntity<Object> response = controller.deleteHistory(token, historyid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, ((ResponseDTO) response.getBody()).getOk());
        assertEquals("History deleted", ((ResponseDTO) response.getBody()).getMessage());

        verify(service, times(1)).deleteHistory(token, historyid);
    }

    @Test
    void testDeleteHistory_NotFound() throws Exception {
        String token = "test-token";
        Integer historyid = 1;
        doThrow(new NoSuchElementException("History not found")).when(service).deleteHistory(token, historyid);

        ResponseEntity<Object> response = controller.deleteHistory(token, historyid);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("History not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testDeleteHistory_Unauthorized() throws Exception {
        // Arrange
        String token = "test-token";
        Integer historyid = 1;
        doThrow(new AuthenticationException("Unauthorized")).when(service).deleteHistory(token, historyid);

        // Act
        ResponseEntity<Object> response = controller.deleteHistory(token, historyid);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testDeleteHistory_BadRequest() throws Exception {
        String token = "test-token";
        Integer historyId = 1;
        doThrow(new BadRequestException("Bad request")).when(service).deleteHistory(token, historyId);

        ResponseEntity<Object> response = controller.deleteHistory(token, historyId);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", ((ResponseDTO) response.getBody()).getMessage());
    }
}
