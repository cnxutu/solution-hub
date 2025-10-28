package com.cv.solution.jwtlogin.auth.pojo.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LoginVO 类简要描述
 *
 * @author xutu
 * @date 2025-10-11 12:13:01
 */
@Data
@SuperBuilder
public class LoginVO {

    public LoginVO() {
    }

    /** JWT Token */
    private String token;

    /** 用户名 */
    private String username;

    /** 用户真实姓名 */
    private String realName;

    /** 所属租户 */
    private Long tenantId;

    /** 用户角色 */
    private List<String> roles;

    /** 登录时间 */
    private LocalDateTime loginTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /**
     * 用户权限列表（角色 / 权限标识）
     */
    private List<String> authorities;

}