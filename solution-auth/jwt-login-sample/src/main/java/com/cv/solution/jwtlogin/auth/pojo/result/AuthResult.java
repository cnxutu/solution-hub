package com.cv.solution.jwtlogin.auth.pojo.result;

import com.cv.solution.jwtlogin.auth.pojo.dto.AuthVerifyDTO;

/**
 * @author: xutu
 * @since: 2025/10/24 08:59
 */
public class AuthResult<T> {
    /** 认证凭证对象 */
    private AuthVerifyDTO verifyDTO;

    /** 用户信息，泛型支持多种 VO 类型 */
    private T userInfo;
}
