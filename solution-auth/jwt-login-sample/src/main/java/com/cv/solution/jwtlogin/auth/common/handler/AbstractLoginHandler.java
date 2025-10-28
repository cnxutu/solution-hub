package com.cv.solution.jwtlogin.auth.common.handler;

import com.cv.boot.common.exception.BizException;
import com.cv.solution.jwtlogin.auth.pojo.dto.UserDetailDTO;
import com.cv.solution.jwtlogin.auth.pojo.query.LoginQuery;
import com.cv.solution.jwtlogin.auth.pojo.result.AuthResult;
import com.cv.solution.jwtlogin.auth.pojo.vo.LoginVO;
import com.cv.solution.jwtlogin.auth.service.ILoginFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 登录流程模板抽象类
 *
 * @author: xutu
 * @since: 2025/10/10 17:36
 */
@Slf4j
public abstract class AbstractLoginHandler<R extends LoginVO> implements ILoginFlow<LoginQuery, R> {

    @Autowired
    protected TokenService tokenService;

    /**
     * 登录模板主流程
     */
    public final AuthResult<R> login(LoginQuery query) {
        long start = System.currentTimeMillis();
        String type = this.getLoginType().name();

        try {
            // 1️⃣ 参数校验
            validateLoginParams(query);

            // 2️⃣ 前置校验（验证码、签名等）
            preLoginCheck(query);

            // 3️⃣ 用户认证
            UserDetailDTO user = authenticate(query);

            // 4️⃣ 后置处理（日志、来源统计）
            afterAuthenticated(user, query);

            // 5️⃣ 生成 Token
//            String token = tokenService.generateToken(user);
            String token = null;

            // 6️⃣ 构建响应结果
            AuthResult<R> result = buildResult(user, token);

            log.info("[{}] 登录成功 userId={} cost={}ms",
                    type, user.getId(), System.currentTimeMillis() - start);
            return result;

        } catch (BizException e) {
            log.warn("[{}] 登录失败：{}", type, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[{}] 登录异常：{}", type, e.getMessage(), e);
            throw new BizException("登录过程发生系统异常");
        }
    }

    /**
     * 构建最终返回结果
     */
    protected abstract AuthResult<R> buildResult(UserDetailDTO user, String token);
}
