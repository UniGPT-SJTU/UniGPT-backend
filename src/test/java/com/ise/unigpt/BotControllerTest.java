package com.ise.unigpt;

import com.google.gson.Gson;
import com.ise.unigpt.controller.BotController;
import com.ise.unigpt.dto.*;
import com.ise.unigpt.service.BotService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;


import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BotController.class)
public class BotControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BotService botService;

    private Gson gson;

    private BotBriefInfoDTO botBriefInfoDTO;
    private BotDetailInfoDTO botDetailInfoDTO;
    private BotEditInfoDTO botEditInfoDTO;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        botBriefInfoDTO = new BotBriefInfoDTO(1, "bot1", "des1", "avatar1");
        botDetailInfoDTO = new BotDetailInfoDTO(
                1,
                "bot1",
                "creator1",
                "des1",
                "detail1",
                "avatar1",
                "baseModelAPI1",
                1,
                1
        );
        botEditInfoDTO = new BotEditInfoDTO(
                "bot1",
                "avatar1",
                "des1",
                "baseModelAPI1",
                true,
                "detail1",
                true
        );

        Mockito.when(botService.getBotBriefInfo(1))
                .thenReturn(botBriefInfoDTO);
        Mockito.when(botService.getBotDetailInfo(1, "token"))
                .thenReturn(botDetailInfoDTO);

        Mockito.when(botService.getBotEditInfo(1, "token"))
                .thenReturn(botEditInfoDTO);

        Mockito.when(botService.getBotDetailInfo(1, "token"))
                .thenReturn(botDetailInfoDTO);

        Mockito.when(botService.getBotEditInfo(1, "token"))
                .thenReturn(botEditInfoDTO);
    }

    @Test
    void shouldGetBotsOk() throws Exception {
        List<BotBriefInfoDTO> botBriefInfoDTOList = new ArrayList<>();
        botBriefInfoDTOList.add(new BotBriefInfoDTO(1, "bot1", "des1", "avatar1"));
        botBriefInfoDTOList.add(new BotBriefInfoDTO(2, "bot2", "des2", "avatar2"));
        Mockito.when(botService.getBots("test", "latest", 1, 20))
                .thenReturn(new GetBotsOkResponseDTO(botBriefInfoDTOList));
        mockMvc.perform(get("/api/bots")
                .param("q", "test")
                .param("order", "latest")
                .param("page", "1")
                .param("pagesize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is(2)));
    }
    @Test
    void shouldGetBotsErrorWhenOrderIsInvalid() throws Exception {
        Mockito.when(botService.getBots("test", "wrong", 1, 20))
                .thenThrow(new IllegalArgumentException("Invalid order parameter"));
        mockMvc.perform(get("/api/bots")
                .param("q", "test")
                .param("order", "wrong")
                .param("page", "1")
                .param("pagesize", "20"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.ok", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid order parameter")));
    }

    @Test
    void shouldGetBotBriefProfileOk() throws Exception {

        mockMvc.perform(get("/api/bots/1")
                .param("info", "brief")
                .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(botBriefInfoDTO)));
    }

    @Test
    void shouldGetBotDetailProfileOk() throws Exception {
        mockMvc.perform(get("/api/bots/1")
                .param("info", "detail")
                .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(botDetailInfoDTO)));
    }

    @Test
    void shouldGetBotEditProfileOk() throws Exception  {
        mockMvc.perform(get("/api/bots/1")
                .param("info", "edit")
                .cookie(new Cookie("token", "token")))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(botEditInfoDTO)));
    }

    @Test
    void shouldGetBotProfileErrorWhenInfoIsInvalid() throws Exception {
        mockMvc.perform(get("/api/bots/1")
                .param("info", "wrong")
                .cookie(new Cookie("token", "token")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ok", is(false)))
                .andExpect(jsonPath("$.message", is("Invalid info parameter")));
    }

    @Test
    public void shouldCreateBotOk() throws Exception {
        Mockito.when(botService.createBot(botEditInfoDTO, "validToken")).thenReturn(new ResponseDTO(true, "Bot created"));

        mockMvc.perform(post("/api/bots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(botEditInfoDTO))
                .cookie(new Cookie("token", "validToken")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok", is(true)))
                .andExpect(jsonPath("$.message", is("Bot created")));

    }
    // TODO: 先测service层代码


}
