package com.ise.unigpt.service;

import com.ise.unigpt.dto.GetPluginsOkResponseDTO;
import com.ise.unigpt.dto.PluginCreateDTO;
import com.ise.unigpt.dto.PluginDetailInfoDTO;
import com.ise.unigpt.dto.PluginEditInfoDTO;
import com.ise.unigpt.dto.ResponseDTO;

public interface PluginService {

    /**
     * @brief 获取插件列表
     * @param q 搜索关键字
     * @param order 排序方式
     * @param page 页码
     * @param pageSize 每页大小
     * @return 插件列表
     */
    GetPluginsOkResponseDTO getPlugins(String q, String order, Integer page, Integer pageSize);

    /**
     * @brief 获取插件详细信息
     * @param id 插件id
     * @param token 用户token
     * @return 插件详细信息
     */
    PluginDetailInfoDTO getPluginInfo(Integer id, String token);

    /**
     * @brief 获取插件编辑信息
     * @param id 插件id
     * @param token 用户token
     * @return 插件编辑信息
     */
    PluginEditInfoDTO getPluginEditInfo(Integer id, String token);

    /**
     * @brief 创建插件
     * @param dto 插件编辑信息
     * @param token 用户token
     * @return 创建结果
     */
    ResponseDTO createPlugin(PluginCreateDTO dto, String token) throws Exception;

}
