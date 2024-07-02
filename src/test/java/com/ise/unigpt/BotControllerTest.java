package com.ise.unigpt;

import com.ise.unigpt.controller.BotController;
import com.ise.unigpt.dto.BotBriefInfoDTO;
import com.ise.unigpt.dto.BotDetailInfoDTO;
import com.ise.unigpt.dto.BotEditInfoDTO;
import com.ise.unigpt.dto.CommentDTO;
import com.ise.unigpt.dto.PromptDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.History;
import com.ise.unigpt.service.BotService;
import com.ise.unigpt.utils.TestBotFactory;
import com.ise.unigpt.utils.TestHistoryFactory;
import com.ise.unigpt.dto.CommentRequestDTO;
import com.ise.unigpt.dto.CreateBotHistoryOkResponseDTO;
import com.ise.unigpt.dto.GetBotHistoryOkResponseDTO;
import com.ise.unigpt.dto.GetBotsOkResponseDTO;
import com.ise.unigpt.dto.GetCommentsOkResponseDTO;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BotControllerTest {

    @Mock
    private BotService service;

    @InjectMocks
    private BotController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBots() {
        List<BotBriefInfoDTO> bots = Arrays.asList(
                TestBotFactory.createBotBriefInfoDTO(),
                TestBotFactory.createBotBriefInfoDTO2(),
                TestBotFactory.createBotBriefInfoDTO3());
        when(service.getBots("", "latest", 0, 20)).thenReturn(new GetBotsOkResponseDTO(bots.size(), bots));
        ResponseEntity<Object> response = controller.getBots("", "latest", 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBots_NotFound() {
        when(service.getBots("", "latest", 0, 20)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.getBots("", "latest", 0, 20);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetBotProfile_Brief() {
        BotBriefInfoDTO bot = new BotBriefInfoDTO(1, "bot1", "description1", "avatar1", false, false);
        when(service.getBotBriefInfo(1, "token")).thenReturn(bot);

        ResponseEntity<Object> response = controller.getBotProfile(1, "brief", "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBotProfile_Detail() throws Exception {
        BotDetailInfoDTO botDetailInfo = TestBotFactory.createBotDetailInfoDTO();
        when(service.getBotDetailInfo(1, "token")).thenReturn(botDetailInfo);

        ResponseEntity<Object> response = controller.getBotProfile(1, "detail", "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBotProfile_Edit() throws Exception {
        BotEditInfoDTO botEditInfo = TestBotFactory.createBotEditInfoDTO();
        when(service.getBotEditInfo(1, "token")).thenReturn(botEditInfo);

        ResponseEntity<Object> response = controller.getBotProfile(1, "edit", "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void testGetBotProfile_InvalidInfo() {
        ResponseEntity<Object> response = controller.getBotProfile(1, "invalid", "token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid info parameter", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetBotProfile_NotFound() {
        when(service.getBotBriefInfo(1, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.getBotProfile(1, "brief", "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateBot() throws Exception {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        ResponseDTO responseDTO = new ResponseDTO(true, "Success");
        when(service.createBot(dto, "token")).thenReturn(responseDTO);

        ResponseEntity<ResponseDTO> response = controller.createBot(dto, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
    }

    @Test
    void testCreateBot_BadRequest() throws Exception {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        when(service.createBot(dto, "token")).thenThrow(new NoSuchElementException("Bad request"));

        ResponseEntity<ResponseDTO> response = controller.createBot(dto, "token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", ((ResponseDTO) response.getBody()).getMessage());
    }
    @Test
    void testUpdateBot() {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        when(service.updateBot(1, dto, "token")).thenReturn(new ResponseDTO(true, "Update success"));
        ResponseEntity<Object> response = controller.updateBot(1, dto, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateBot_NotFound() {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        when(service.updateBot(1, dto, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.updateBot(1, dto, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testUpdateBot_BadRequest() {
        BotEditInfoDTO dto = new BotEditInfoDTO();
        when(service.updateBot(1, dto, "token")).thenThrow(new IllegalArgumentException("Bad request"));

        ResponseEntity<Object> response = controller.updateBot(1, dto, "token");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testLikeBot() {
        when(service.likeBot(1, "token")).thenReturn(new ResponseDTO(true, "Liked"));

        ResponseEntity<Object> response = controller.likeBot(1, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLikeBot_NotFound() {
        when(service.likeBot(1, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.likeBot(1, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testDislikeBot() {
        when(service.dislikeBot(1, "token")).thenReturn(new ResponseDTO(true, "Disliked"));

        ResponseEntity<Object> response = controller.dislikeBot(1, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testDislikeBot_NotFound() {
        when(service.dislikeBot(1, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.dislikeBot(1, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testStarBot() {
        when(service.starBot(1, "token")).thenReturn(new ResponseDTO(true, "Starred"));

        ResponseEntity<Object> response = controller.starBot(1, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testStarBot_NotFound() {
        when(service.starBot(1, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.starBot(1, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testUnstarBot() {
        when(service.unstarBot(1, "token")).thenReturn(new ResponseDTO(true, "Unstarred"));

        ResponseEntity<Object> response = controller.unstarBot(1, "token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUnstarBot_NotFound() {
        when(service.unstarBot(1, "token")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.unstarBot(1, "token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetComments() {
        List<CommentDTO> comments = Arrays.asList(
                new CommentDTO(1, "Hello", new Date(), "avatar1", "creeper", 2, 1),
                new CommentDTO(2, "World", new Date(), "avatar2", "awman", 3, 2),
                new CommentDTO(3, "Test", new Date(), "avatar3", "so we back in the mine", 4, 3));
        when(service.getComments(1, 0, 100)).thenReturn(new GetCommentsOkResponseDTO(comments.size(), comments));

        ResponseEntity<Object> response = controller.getComments(1, 0, 100);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetComments_NotFound() {
        when(service.getComments(1, 0, 100)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.getComments(1, 0, 100);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateComment() {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setContent("Test comment");
        when(service.createComment(1, "token", "Test comment")).thenReturn(new ResponseDTO(true, "Comment created"));

        ResponseEntity<Object> response = controller.createComment(1, "token", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateComment_NotFound() {
        CommentRequestDTO request = new CommentRequestDTO();
        request.setContent("Test comment");
        when(service.createComment(1, "token", "Test comment")).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.createComment(1, "token", request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testGetBotHistory() throws Exception {
        List<History> historyList = Arrays.asList(
                TestHistoryFactory.CreateHistory(),
                TestHistoryFactory.CreateHistory());
        when(service.getBotHistory(1, "token", 0, 20))
                .thenReturn(new GetBotHistoryOkResponseDTO(historyList.size(), historyList));

        ResponseEntity<Object> response = controller.getBotHistory(1, "token", 0, 20);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetBotHistory_NotFound() {
        when(service.getBotHistory(1, "token", 0, 20)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.getBotHistory(1, "token", 0, 20);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateBotHistory() throws Exception {
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("key1", "value1"),
                new PromptDTO("key2", "value2"));
        when(service.createBotHistory(1, "token", promptList))
                .thenReturn(new CreateBotHistoryOkResponseDTO(true, "Success", 1, "userAsk"));

        ResponseEntity<Object> response = controller.createBotHistory(1, "token", promptList);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreateBotHistory_NotFound() throws Exception {
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("key1", "value1"),
                new PromptDTO("key2", "value2"));
        when(service.createBotHistory(1, "token", promptList)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.createBotHistory(1, "token", promptList);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateBotHistory_NoSuchElementException() throws Exception {
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("key1", "value1"),
                new PromptDTO("key2", "value2"));
        when(service.createBotHistory(1, "token", promptList)).thenThrow(new NoSuchElementException("Not found"));

        ResponseEntity<Object> response = controller.createBotHistory(1, "token", promptList);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", ((ResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void testCreateBotHistory_BadRequestException() throws Exception {
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("key1", "value1"),
                new PromptDTO("key2", "value2"));
        when(service.createBotHistory(1, "token", promptList)).thenThrow(new BadRequestException("Bad request"));

        ResponseEntity<Object> response = controller.createBotHistory(1, "token", promptList);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad request", ((ResponseDTO) response.getBody()).getMessage());
    }
}
