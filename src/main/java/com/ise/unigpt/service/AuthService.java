package com.ise.unigpt.service;

import com.ise.unigpt.model.User;

import javax.naming.AuthenticationException;

public interface AuthService {

    /**
     * @brief 根据token获取用户
     * @param token 用户token
     * @return 用户
     */
    User getUserByToken(String token);

    /**
     * @brief Jaccount登录函数
     * @param code Jaccount返回的code
     * @return 若登录成功，返回更新后的token
     * @throws AuthenticationException 登录异常
     */
    String jaccountLogin(String code) throws AuthenticationException;

    /**
     * @brief 用于端到端测试的登录函数
     * @param password 测试登录秘钥
     * @return
     * @throws AuthenticationException
     */
    String testLogin(String password) throws AuthenticationException;
}
