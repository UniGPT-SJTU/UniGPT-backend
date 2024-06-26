package com.ise.unigpt;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.BotRepository;
import com.ise.unigpt.repository.HistoryRepository;
import com.ise.unigpt.repository.PromptChatRepository;
import com.ise.unigpt.repository.UserRepository;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.serviceimpl.BotServiceImpl;
import com.ise.unigpt.utils.TestBotFactory;
import com.ise.unigpt.utils.TestCommentFactory;
import com.ise.unigpt.utils.TestHistoryFactory;
import com.ise.unigpt.utils.TestUserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BotServiceTest {

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
    private BotServiceImpl botService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBots_latest() {
        List<Bot> bots = Arrays.asList(
                TestBotFactory.createBot(),
                TestBotFactory.createBot2(),
                TestBotFactory.createBot3());
        List<BotBriefInfoDTO> botBriefInfoDTOs = Arrays.asList(
                TestBotFactory.createBotBriefInfoDTO(),
                TestBotFactory.createBotBriefInfoDTO2(),
                TestBotFactory.createBotBriefInfoDTO3());
        when(botRepository.findAllByOrderByIdDesc()).thenReturn(bots);
        GetBotsOkResponseDTO response = botService.getBots("", "latest", 0, 20);

        assertEquals(3, response.getTotal());
        assertEquals(botBriefInfoDTOs, response.getBots());
    }

    @Test
    void testGetBots_like() {
        List<Bot> bots = Arrays.asList(
                TestBotFactory.createBot(),
                TestBotFactory.createBot2(),
                TestBotFactory.createBot3());
        List<BotBriefInfoDTO> botBriefInfoDTOs = Arrays.asList(
                TestBotFactory.createBotBriefInfoDTO(),
                TestBotFactory.createBotBriefInfoDTO2(),
                TestBotFactory.createBotBriefInfoDTO3());
        when(botRepository.findAllByOrderByLikeNumberDesc()).thenReturn(bots);
        GetBotsOkResponseDTO response = botService.getBots("", "like", 0, 20);

        assertEquals(3, response.getTotal());
        assertEquals(botBriefInfoDTOs, response.getBots());
    }

    @Test
    void testGetBots_query() {
        List<Bot> bots = Arrays.asList(
                TestBotFactory.createBot(),
                TestBotFactory.createBot2(),
                TestBotFactory.createBot3());
        List<BotBriefInfoDTO> botBriefInfoDTOs = List.of(
                TestBotFactory.createBotBriefInfoDTO2());
        when(botRepository.findAllByOrderByLikeNumberDesc()).thenReturn(bots);
        GetBotsOkResponseDTO response = botService.getBots("2", "like", 0, 20);

        assertEquals(1, response.getTotal());
        assertEquals(botBriefInfoDTOs, response.getBots());
    }

    @Test
    void testGetBotBriefInfo() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotBriefInfoDTO botBriefInfoDTO = TestBotFactory.createBotBriefInfoDTO_asCreator();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotBriefInfoDTO response = botService.getBotBriefInfo(1, "token");

        assertEquals(botBriefInfoDTO, response);
    }

    @Test
    void testGetBotDetailInfo() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotDetailInfoDTO botDetailInfoDTO = TestBotFactory.createBotDetailInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotDetailInfoDTO response = botService.getBotDetailInfo(1, "token");

        assertEquals(botDetailInfoDTO, response);
    }

    @Test
    void testGetBotEditInfo_creator() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotEditInfoDTO botEditInfoDTO = TestBotFactory.createBotEditInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotEditInfoDTO response = botService.getBotEditInfo(1, "token");

        assertEquals(botEditInfoDTO, response);
    }

    @Test
    void testGetBotEditInfo_notCreator() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser2();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);

        try {
            botService.getBotEditInfo(1, "token");
        } catch (Exception e) {
            assertEquals("Bot not published for ID: 1", e.getMessage());
        }
    }

    @Test
    void testGetBotEditInfo_admin() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createAdmin();
        BotEditInfoDTO botEditInfoDTO = TestBotFactory.createBotEditInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotEditInfoDTO response = botService.getBotEditInfo(1, "token");

        assertEquals(botEditInfoDTO, response);
    }

    @Test
    void testCreateBot() {
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.createBot(dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testUpdateBot_creator() {
        Bot bot = TestBotFactory.createBot();
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createUser();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.updateBot(1, dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testUpdateBot_admin() {
        Bot bot = TestBotFactory.createBot();
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createAdmin();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.updateBot(1, dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testLikeBot() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser2();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.likeBot(1, "token");

        assertEquals(2, bot.getLikeNumber());
        assertEquals(2, bot.getLikeUsers().size());
        assertEquals(true, response.getOk());
    }

    @Test
    void testDislikeBot() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.dislikeBot(1, "token");

        assertEquals(0, bot.getLikeNumber());
        assertEquals(0, bot.getLikeUsers().size());
        assertEquals(true, response.getOk());
    }

    @Test
    void testStarBot() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser2();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.starBot(1, "token");

        assertEquals(2, bot.getStarNumber());
        assertEquals(2, bot.getStarUsers().size());
        assertEquals(true, response.getOk());
    }

    @Test
    void testUnstarBot() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.unstarBot(1, "token");

        assertEquals(0, bot.getStarNumber());
        assertEquals(0, bot.getStarUsers().size());
        assertEquals(true, response.getOk());
    }

    @Test
    void testLikeAlreadyLikeBot() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.likeBot(1, "token");

        assertEquals(1, bot.getLikeUsers().size());
        assertEquals(false, response.getOk());
    }

    @Test
    void testGetBotHistory() {
        List<History> histories = new ArrayList<>(
                List.of(TestHistoryFactory.CreateHistory()));
        Bot bot = TestBotFactory.createBot();
        histories.get(0).setBot(bot);
        User user = TestUserFactory.createUser();
        user.setHistories(histories);
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        GetBotHistoryOkResponseDTO response = botService.getBotHistory(1, "token", 0, 20);

        assertEquals(1, response.getTotal());
    }

    @Test
    void testCreateBotHistory() {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        List<PromptDTO> promptList = Arrays.asList(
                new PromptDTO("prompt1", "response1"),
                new PromptDTO("prompt2", "response2"));
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));

        try {
            CreateBotHistoryOkResponseDTO response = botService.createBotHistory(1, "token", promptList);
            assertEquals(true, response.getOk());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testGetComments() {
        List<Comment> comments = List.of(
                TestCommentFactory.createComment(TestUserFactory.createUser(), TestBotFactory.createBot()));
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(TestBotFactory.createBot()));
        GetCommentsOkResponseDTO response = botService.getComments(1, 0, 100);

        assertEquals(1, response.getTotal());
        assertEquals(comments.get(0).getContent(), response.getComments().get(0).getContent());
    }

    @Test
    void testCreateComment() {
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(TestBotFactory.createBot()));
        ResponseDTO response = botService.createComment(1, "token", "Test comment");

        assertEquals(true, response.getOk());
    }

}
