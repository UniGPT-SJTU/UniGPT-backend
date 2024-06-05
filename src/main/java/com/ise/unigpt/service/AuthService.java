package com.ise.unigpt.service;

import com.ise.unigpt.dto.LoginRequestDTO;
import com.ise.unigpt.dto.RegisterRequestDTO;
import com.ise.unigpt.model.User;

import javax.naming.AuthenticationException;

public interface AuthService {
    /**
     * @brief 用户登录函数
     * @param dto 登录请求
     * @return 若登录成功，返回更新后的token
     * @throws AuthenticationException 登录异常
     */
    /* String login(LoginRequestDTO dto) throws AuthenticationException; */

    /**
     * @brief 用户注册函数
     * @param dto 注册请求
     */
    /* void register(RegisterRequestDTO dto); */

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
}
