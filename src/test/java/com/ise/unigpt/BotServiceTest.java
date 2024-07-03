package com.ise.unigpt;

import com.ise.unigpt.dto.*;
import com.ise.unigpt.model.*;
import com.ise.unigpt.repository.*;
import com.ise.unigpt.service.AuthService;
import com.ise.unigpt.service.ChatHistoryService;
import com.ise.unigpt.serviceimpl.BotServiceImpl;
import com.ise.unigpt.utils.*;
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
import java.util.NoSuchElementException;

class BotServiceTest {

    @Mock
    private BotRepository botRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private MemoryRepository memoryRepository;

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
    void testGetBots_latest() throws Exception {
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
    void testGetBots_like() throws Exception {
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
    void testGetBots_query() throws Exception {
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
    void testGetBots_invalidOrder() throws Exception {
        try {
            botService.getBots("", "invalid", 0, 20);
        } catch (Exception e) {
            assertEquals("Invalid order parameter", e.getMessage());
        }
    }

    @Test
    void testGetBotBriefInfo() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotBriefInfoDTO botBriefInfoDTO = TestBotFactory.createBotBriefInfoDTO_asCreator();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotBriefInfoDTO response = botService.getBotBriefInfo(1, "token");

        assertEquals(botBriefInfoDTO, response);
    }

    @Test
    void testGetBriefInfo_userNotFound() throws Exception {
        Bot bot = TestBotFactory.createBot();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenThrow(new NoSuchElementException("User not found"));

        try {
            botService.getBotBriefInfo(1, "token");
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void testGetBriefInfo_botNotFound() throws Exception {
        when(botRepository.findById(1)).thenThrow(new NoSuchElementException("Bot not found"));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser());

        try {
            botService.getBotBriefInfo(1, "token");
        } catch (Exception e) {
            assertEquals("Bot not found", e.getMessage());
        }
    }

    @Test
    void testGetBriefInfo_botNotPublished() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setIsPublished(false);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(TestUserFactory.createUser2());

        try {
            botService.getBotBriefInfo(1, "token");
        } catch (Exception e) {
            assertEquals("Bot not published for ID: 1", e.getMessage());
        }
    }

    @Test
    void testGetBotDetailInfo() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotDetailInfoDTO botDetailInfoDTO = TestBotFactory.createBotDetailInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotDetailInfoDTO response = botService.getBotDetailInfo(1, "token");

        assertEquals(botDetailInfoDTO, response);
    }

    @Test
    void testGetBotDetailInfo_userNotFound() throws Exception{
        Bot bot = TestBotFactory.createBot();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenThrow(new NoSuchElementException("User not found"));

        try {
            botService.getBotDetailInfo(1, "token");
        } catch (Exception e) {
            assertEquals("User not found", e.getMessage());
        }
    }

    @Test
    void testGetBotDetailInfo_usedBotUnpublishedNow() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setIsPublished(false);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));

        User user = TestUserFactory.createUser2();
        user.setUsedBots(new ArrayList<>(List.of(bot)));
        when(authService.getUserByToken("token")).thenReturn(user);

        try {
            botService.getBotDetailInfo(1, "token");
        } catch (Exception e) {
            assertEquals("Bot not published for ID: 1", e.getMessage());
        }
    }

    @Test
    void testGetBotEditInfo_creator() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        BotEditInfoDTO botEditInfoDTO = TestBotFactory.createBotEditInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotEditInfoDTO response = botService.getBotEditInfo(1, "token");

        assertEquals(botEditInfoDTO, response);
    }

    @Test
    void testGetBotEditInfo_notCreator() throws Exception {
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
    void testGetBotEditInfo_admin() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createAdmin();
        BotEditInfoDTO botEditInfoDTO = TestBotFactory.createBotEditInfoDTO();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        BotEditInfoDTO response = botService.getBotEditInfo(1, "token");

        assertEquals(botEditInfoDTO, response);
    }

    @Test
    void testCreateBot() throws Exception {
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.createBot(dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testUpdateBot_creator() throws Exception {
        Bot bot = TestBotFactory.createBot();
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createUser();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.updateBot(1, dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testUpdateBot_admin() throws Exception {
        Bot bot = TestBotFactory.createBot();
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createAdmin();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO responseDTO = botService.updateBot(1, dto, "token");

        assertEquals(true, responseDTO.getOk());
    }

    @Test
    void testUpdateBot_Unauthorized() throws Exception {
        Bot bot = TestBotFactory.createBot();
        BotEditInfoDTO dto = TestBotFactory.createBotEditInfoDTO();
        User user = TestUserFactory.createUser2();
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);

        try {
            botService.updateBot(1, dto, "token");
        } catch (Exception e) {
            assertEquals("User not authorized to update bot", e.getMessage());
        }
    }

    @Test
    void testLikeBot() throws Exception {
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
    void testDislikeBot() throws Exception {
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
    void testDislikeBot_notLiked() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        user.setLikeBots(new ArrayList<>());
        bot.setLikeNumber(0);
        bot.setLikeUsers(new ArrayList<>());

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.dislikeBot(1, "token");

        assertEquals(0, bot.getLikeNumber());
        assertEquals(0, bot.getLikeUsers().size());
        assertEquals(false, response.getOk());
    }

    @Test
    void testStarBot() throws Exception {
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
    void testStarBot_alreadyStarred() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setStarNumber(0);
        User user = TestUserFactory.createUser();
        user.setStarBots(new ArrayList<>(List.of(bot)));
        bot.setStarUsers(new ArrayList<>(List.of(user)));

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.starBot(1, "token");

        assertEquals(0, bot.getStarNumber());
        assertEquals(1, bot.getStarUsers().size());
        assertEquals(false, response.getOk());
    }

    @Test
    void testUnstarBot() throws Exception {
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
    void testUnstarBot_notStarred() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();
        user.setStarBots(new ArrayList<>());
        bot.setStarNumber(0);
        bot.setStarUsers(new ArrayList<>());

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.unstarBot(1, "token");

        assertEquals(0, bot.getStarNumber());
        assertEquals(0, bot.getStarUsers().size());
        assertEquals(false, response.getOk());
    }

    @Test
    void testLikeAlreadyLikeBot() throws Exception {
        Bot bot = TestBotFactory.createBot();
        User user = TestUserFactory.createUser();

        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));
        when(authService.getUserByToken("token")).thenReturn(user);
        ResponseDTO response = botService.likeBot(1, "token");

        assertEquals(1, bot.getLikeUsers().size());
        assertEquals(false, response.getOk());
    }

    @Test
    void testGetBotHistory() throws Exception {
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
    void testCreateBotHistory() throws Exception {
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
    void testCreateBotHistory_promptListSizeNotMatch() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setPromptKeys(new ArrayList<>(List.of("prompt1", "prompt2")));
        User user = TestUserFactory.createUser();
        List<PromptDTO> promptList = List.of(
                new PromptDTO("prompt1", "response1"));
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));

        try {
            botService.createBotHistory(1, "token", promptList);
        } catch (Exception e) {
            assertEquals("Prompt list not match", e.getMessage());
        }
    }

    @Test
    void testCreateBotHistory_promptListKeyNotMatch() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setPromptKeys(new ArrayList<>(List.of("prompt1", "prompt2")));
        User user = TestUserFactory.createUser();
        List<PromptDTO> promptList = List.of(
                new PromptDTO("prompt3", "response1"),
                new PromptDTO("prompt2", "response2"));
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));

        try {
            botService.createBotHistory(1, "token", promptList);
        } catch (Exception e) {
            assertEquals("Prompt list not match", e.getMessage());
        }
    }

    @Test
    void testCreateBotHistory_lastPromptChatNotUser() throws Exception {
        Bot bot = TestBotFactory.createBot();
        bot.setPromptChats(new ArrayList<>(List.of(
                TestPromptChatFactory.createBotPromptChat(),
                TestPromptChatFactory.createBotPromptChat(),
                TestPromptChatFactory.createBotPromptChat())));
        User user = TestUserFactory.createUser();
        List<PromptDTO> promptList = List.of(
                new PromptDTO("prompt1", "response1"),
                new PromptDTO("prompt2", "response2"));
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(bot));

        try {
            botService.createBotHistory(1, "token", promptList);
        } catch (Exception e) {
            assertEquals("Last prompt chat is not of type USER", e.getMessage());
        }
    }
    @Test
    void testGetComments() throws Exception {
        List<Comment> comments = List.of(
                TestCommentFactory.createComment(TestUserFactory.createUser(), TestBotFactory.createBot()));
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(TestBotFactory.createBot()));
        GetCommentsOkResponseDTO response = botService.getComments(1, 0, 100);

        assertEquals(1, response.getTotal());
        assertEquals(comments.get(0).getContent(), response.getComments().get(0).getContent());
    }

    @Test
    void testCreateComment() throws Exception {
        User user = TestUserFactory.createUser();
        when(authService.getUserByToken("token")).thenReturn(user);
        when(botRepository.findById(1)).thenReturn(java.util.Optional.of(TestBotFactory.createBot()));
        ResponseDTO response = botService.createComment(1, "token", "Test comment");

        assertEquals(true, response.getOk());
    }

}
