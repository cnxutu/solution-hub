package com.cv.solution.jwtlogin.auth.common.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: xutu
 * @since: 2025/10/10 16:01
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret;
    private long expiration; // 秒
}
