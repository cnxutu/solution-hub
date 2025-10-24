package com.cv.devlite.jwtlogin.auth.service;


import cn.hutool.system.UserInfo;
import com.cv.devlite.jwtlogin.auth.common.enums.LoginTypeEnum;
import com.cv.devlite.jwtlogin.auth.pojo.dto.UserDetailDTO;
import com.cv.devlite.jwtlogin.auth.pojo.query.LoginQuery;
import com.cv.devlite.jwtlogin.auth.pojo.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xutu
 * @since: 2025/10/10 17:34
 */
/**
 * 登录流程契约定义
 */
public interface ILoginFlow<Q extends LoginQuery, R extends LoginVO> {

    /** 校验参数 */
    void validateLoginParams(Q query);

    /** 前置校验（验证码、签名验证等，可选） */
    default void preLoginCheck(Q query) {}

    /** 核心认证逻辑（由子类具体实现） */
    UserDetailDTO authenticate(Q query);

    /** 认证成功后的后置操作（可选，如记录日志、来源等） */
    void afterAuthenticated(UserDetailDTO user, Q query);

    /** 登录类型标识（用于工厂选择） */
    LoginTypeEnum getLoginType();
}
