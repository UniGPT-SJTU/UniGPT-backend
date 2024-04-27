package com.ise.unigpt.service;
import com.ise.unigpt.dto.*;

import java.util.List;

public interface BotService {
    /**
     * @brief 获取机器人列表
     * @param q 搜索关键字
     * @param order 排序方式
     * @param page 页码
     * @param pageSize 每页大小
     * @return 机器人列表
     */
    GetBotsOkResponseDTO getBots(String q, String order, Integer page, Integer pageSize);

    /**
     * @brief 获取机器人简要信息
     * @param id 机器人id
     * @return 机器人简要信息
     */
    BotBriefInfoDTO getBotBriefInfo(Integer id);

    /**
     * @brief 获取机器人详细信息
     * @param id 机器人id
     * @param token 用户token
     * @return 机器人详细信息
     */
    BotDetailInfoDTO getBotDetailInfo(Integer id, String token);

    /**
     * @brief 获取机器人编辑信息
     * @param id 机器人id
     * @param token 用户token
     * @return 机器人编辑信息
     */
    BotEditInfoDTO getBotEditInfo(Integer id, String token);

    /**
     * @brief 创建机器人
     * @param dto 机器人编辑信息
     * @param token 用户token
     * @return 创建结果
     */
    ResponseDTO createBot(BotEditInfoDTO dto, String token);

    /**
     * @brief 更新机器人
     * @param id 机器人id
     * @param dto 机器人编辑信息
     * @param token 用户token
     * @return 更新结果
     */
    ResponseDTO updateBot(Integer id, BotEditInfoDTO dto, String token);

    /**
     * @brief 删除机器人
     * @param id 机器人id
     * @param token 用户token
     * @return 删除结果
     */
    ResponseDTO likeBot(Integer id, String token);

    /**
     * @brief 取消点赞机器人
     * @param id 机器人id
     * @param token 用户token
     * @return 取消点赞结果
     */
    ResponseDTO dislikeBot(Integer id, String token);

    /**
     * @brief 收藏机器人
     * @param id 机器人id
     * @param token 用户token
     * @return 收藏结果
     */
    ResponseDTO starBot(Integer id, String token);

    /**
     * @brief 取消收藏机器人
     * @param id 机器人id
     * @param token 用户token
     * @return 取消收藏结果
     */
    ResponseDTO unstarBot(Integer id, String token);

    /**
     * @brief 获取机器人历史记录
     * @param id 机器人id
     * @param token 用户token
     * @param page 页码
     * @param pageSize 每页大小
     * @return 机器人历史记录
     */
    GetBotHistoryOkResponseDTO getBotHistory(Integer id, String token, Integer page,Integer pageSize);

    //TODO: 此处接口有些模糊
    /**
     * @brief 添加机器人历史记录
     * @param id 机器人id
     * @param token 用户token
     * @param contentList 历史记录内容
     * @return 添加结果
     */
    ResponseDTO createChatHistory(Integer id, String token, List<String> contentList);

    /**s
     * @brief 获取机器人评论
     * @param id 机器人id
     * @param page 页码
     * @param pageSize 每页大小
     * @return 机器人评论
     */
    GetCommentsOkResponseDTO getComments(Integer id, Integer page, Integer pageSize);

    /**
     * @brief 创建评论
     * @param id 机器人id
     * @param token 用户token
     * @param content 评论内容
     * @return 创建结果
     */
    ResponseDTO createComment(Integer id, String token, String content) ;
}
