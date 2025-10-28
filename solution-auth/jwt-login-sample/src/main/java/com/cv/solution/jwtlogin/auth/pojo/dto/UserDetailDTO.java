package com.cv.solution.jwtlogin.auth.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: xutu
 * @since: 2025/10/23 17:58
 */
@Data
public class UserDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录密码（建议加 @JsonIgnore）
     */
    private String password;

    /**
     * 所属租户ID（多租户场景）
     */
    private Long tenantId;

    /**
     * 用户真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    private List<String> roles;

}
