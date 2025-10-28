package com.cv.solution.jwtlogin.auth.pojo.vo;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * WeChatLoginVO 类简要描述
 *
 * @author xutu
 * @date 2025-10-11 12:12:47
 */
@Data
@SuperBuilder
public class WeChatLoginVO extends LoginVO {

    public WeChatLoginVO() {
    }

    /**
     * 微信用户特有信息，可根据业务扩展
     */
    private String openId;
    private String sessionKey;
    private String nickname;
    private String avatarUrl;

}