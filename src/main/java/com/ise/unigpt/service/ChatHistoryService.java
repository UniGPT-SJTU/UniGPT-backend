package com.ise.unigpt.service;

import com.ise.unigpt.dto.GetChatsOkResponseDTO;
import com.ise.unigpt.dto.PromptDTO;
import com.ise.unigpt.model.*;

import javax.naming.AuthenticationException;


import java.util.List;

public interface ChatHistoryService {


    /**
     * @brief 删除末尾的若干个对话
     * @param historyId 历史id
     * @param n         删除数量
     * @param token     用户token
     */
    void deleteChats(Integer historyId, Integer n, String token) throws AuthenticationException;

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

    // /**
    //  * @brief 修改指定历史的提示列表
    //  * @param historyid  历史id
    //  * @param promptList 提示列表
    //  * @return 修改结果
    //  */
    // ResponseDTO updatePromptList(Integer historyid, List<PromptDTO> promptList) throws BadRequestException;

    /**
     * @brief 获取指定历史的历史类
     * @param historyId 历史id
     * @return 历史类 GetHistoryDTO
     */
    History getHistory(Integer historyId);

    /**
     * @brief 更新历史的最近活跃时间
     * @param history
     */
    void updateHistoryActiveTime(History history);


    /**
     * @brief 删除对话历史
     * @param token 用户token
     * @param historyId 删除的历史id
     * @throws Exception
     */
    void deleteHistory(String token, Integer historyId) throws Exception;
}
