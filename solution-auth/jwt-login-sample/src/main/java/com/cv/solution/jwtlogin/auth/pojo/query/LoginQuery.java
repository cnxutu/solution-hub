package com.cv.solution.jwtlogin.auth.pojo.query;

import lombok.Data;

/**
 * @author: xutu
 * @since: 2025/10/10 16:18
 */
@Data
public class LoginQuery {

    private String username;

    private String password;

    /**
     * 登录类型：
     * @see com.cv.kb.auth.common.enums.LoginTypeEnum
     */
    private Integer loginType;


    // Web 登录特有
    private String captcha;

    // WeChat 登录特有
    private String openId;



}
