package com.cv.solution.jwtlogin.auth.common.enums;

import com.cv.boot.common.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author: xutu
 * @since: 2025/10/24 08:06
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {
    WEB(1, "Web 登录"),

    WECHAT(2, "微信登录"),

    APP(3, "App 登录");

    private final int code;
    private final String desc;

    public static LoginTypeEnum of(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new BizException("不支持的登录类型: " + code));
    }
}
