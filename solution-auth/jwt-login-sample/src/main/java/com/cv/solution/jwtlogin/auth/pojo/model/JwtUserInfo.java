package com.cv.solution.jwtlogin.auth.pojo.model;

import com.cv.solution.jwtlogin.auth.pojo.dto.UserDetailDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * JwtUserInfo 类简要描述
 * <p>
 * JWT 中存储的用户信息对象（轻量载体）
 *
 * @author xutu
 * @date 2025-10-12 09:35:55
 */
@Data
public class JwtUserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;      // 用户ID
    private String username;  // 用户名
    private Long tenantId;    // 租户ID
    private List<String> roles; // 角色信息（可用于前端菜单控制）

    /**
     * 根据 UserDetailDTO 构建 JwtUserInfo
     */
    public static JwtUserInfo fromUserDetail(UserDetailDTO userDetail) {
        if (userDetail == null) return null;

        JwtUserInfo info = new JwtUserInfo();
        info.setUserId(userDetail.getId());
        info.setUsername(userDetail.getUsername());
        info.setTenantId(userDetail.getTenantId());
        info.setRoles(userDetail.getRoles());
        return info;
    }

}
