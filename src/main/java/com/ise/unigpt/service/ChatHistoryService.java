package com.ise.unigpt.service;

import com.ise.unigpt.dto.GetChatsOkResponseDTO;
import com.ise.unigpt.dto.PromptDTO;
import com.ise.unigpt.dto.ResponseDTO;
import com.ise.unigpt.model.*;
import com.ise.unigpt.dto.GetHistoryDTO;

import javax.naming.AuthenticationException;

import org.apache.coyote.BadRequestException;

import java.util.List;

public interface ChatHistoryService {

    /**
     * @brief 在指定历史中加入对话
     * @param historyId 历史id
     * @param content   对话的内容
     * @param type      对话的种类(USER, BOT)
     */
    void createChat(Integer historyId, String content, ChatType type, String token) throws AuthenticationException;

    /**
     * @brief 获取指定历史的所有对话列表
     * @param historyId 历史id
     * @return 对话的列表
     */
    GetChatsOkResponseDTO getChats(
            Integer historyId,
            Integer page,
            Integer pageSize,
            String token)
            throws AuthenticationException;

    /**
     * @brief 获取指定历史的所有对话列表
     * @param historyid 历史id
     * @return 对话的列表
     */
    List<PromptDTO> getPromptList(Integer historyid);

    /**
     * @brief 修改指定历史的提示列表
     * @param historyid  历史id
     * @param promptList 提示列表
     * @return 修改结果
     */
    ResponseDTO updatePromptList(Integer historyid, List<PromptDTO> promptList) throws BadRequestException;

    /**
     * @brief 获取指定历史的历史类
     * @param historyId 历史id
     * @return 历史类 GetHistoryDTO
     */
    History getHistory(Integer historyId);
}
