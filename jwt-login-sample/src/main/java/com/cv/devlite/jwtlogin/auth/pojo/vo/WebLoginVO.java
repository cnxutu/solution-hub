package com.cv.devlite.jwtlogin.auth.pojo.vo;

import com.cv.devlite.jwtlogin.auth.pojo.dto.UserDetailDTO;
import com.cv.devlite.jwtlogin.auth.common.util.JwtUtils;
import com.cv.devlite.jwtlogin.auth.pojo.model.JwtUserInfo;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * WebLoginVO 类简要描述
 *
 * @author xutu
 * @date 2025-10-11 12:13:24
 */
@Data
@SuperBuilder
public class WebLoginVO extends LoginVO {

    private Long loginTimeMillis; // Web 特有：毫秒时间
    private String loginIp;       // Web 特有：IP


    /**
     * 根据 Spring Security 的 Authentication 构建 WebLoginVO
     */
    public static WebLoginVO fromAuthentication(UserDetailDTO user, JwtUtils jwtUtils, String loginIp) {
        String token = jwtUtils.generateToken(JwtUserInfo.fromUserDetail(user));

        return WebLoginVO.builder()
                .token(token)
                .username(user.getUsername())
                .loginTimeMillis(System.currentTimeMillis())
                .loginIp(loginIp)
                .build();
    }


}