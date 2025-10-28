package com.cv.solution.jwtlogin.auth.common.handler;

import com.cv.kb.auth.common.enums.LoginTypeEnum;
import com.cv.kb.auth.login.pojo.query.LoginQuery;
import com.cv.kb.auth.login.pojo.vo.WeChatLoginVO;
import com.cv.kb.auth.security.pojo.dto.UserDetailDTO;
import com.cv.kb.auth.security.service.impl.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author: xutu
 * @since: 2025/10/10 17:41
 */
@Slf4j
@Component
public class WeChatLoginHandler extends AbstractLoginHandler<WeChatLoginVO> {

    private final CustomUserDetailsService userService;
    private final AuthenticationManager authManager;

    public WeChatLoginHandler(CustomUserDetailsService userService,
                              AuthenticationManager authManager) {
        this.userService = userService;
        this.authManager = authManager;
    }

    /**
     * 支持的登录类型
     */
    @Override
    public boolean supports(LoginQuery request) {
        return request.getLoginType() != null && request.getLoginType() == LoginTypeEnum.WECHAT.getCode();
    }

    /**
     * 构建 Authentication 对象，提交给 Spring Security
     */
    @Override
    public Authentication buildAuthentication(LoginQuery request) {
        // 1️⃣ 从 LoginQuery 中获取 openId
        String openId = request.getUsername(); // 假设微信登录这里用 username 存 openId

        // 2️⃣ 查询用户信息
        UserDetailDTO user = userService.loadUserByWeChatOpenId(openId);

        // 3️⃣ 构建认证对象（密码可以为空或固定值）
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    /**
     * 登录成功后处理
     */
    @Override
    public WeChatLoginVO buildLoginResponse(Authentication authResult) {
        UserDetailDTO user = (UserDetailDTO) authResult.getPrincipal();
        // 返回 JWT 或自定义登录信息
        WeChatLoginVO weChatLoginVO = new WeChatLoginVO();
        weChatLoginVO.setToken(authResult.getCredentials().toString());
        return weChatLoginVO;
    }

    /**
     * 通用前置校验（等保策略、账号状态等）
     * <p>
     * Java 方法覆盖规则：
     * 当子类 重写（Override） 父类方法时，子类的方法会完全覆盖父类方法
     * 父类的方法不会自动执行，除非在子类中显式调用 super.method(...)
     * 通用前置校验（可选，等保或限制策略）
     */
    @Override
    public void preAuthenticationCheck(LoginQuery request) {
        // 调用父类通用登录校验方法
        super.preAuthenticationCheck(request);
        // 执行子类特有的登录校验方法
        // 例如：检查 openId 是否为空
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new AuthenticationServiceException("OpenId不能为空");
        }
    }

    /**
     * 登录失败逻辑，可记录失败次数或触发告警
     */
    @Override
    public void onLoginFailure(LoginQuery request, AuthenticationException exception) {
        super.recordLoginFail(request);
        log.warn("[WeChatLogin] 用户 {} 登录失败: {}", request.getUsername(), exception.getMessage());
    }
}



