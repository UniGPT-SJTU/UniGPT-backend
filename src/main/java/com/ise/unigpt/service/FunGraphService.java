package com.ise.unigpt.service;

import java.util.List;

public interface FunGraphService {

    /**
     * @brief 调用python函数（目前为HUAWEI cloud function graph容器实现）
     * @param moduleName 模块名称
     * @param functionName 函数名
     * @param params 参数列表
     * @param urn 函数urn
     * @return
     */
    public String invokeFunction(String username, String moduleName, String functionName, List<String> params, String urn);
}
