package com.cv.solution.jwtlogin.auth.common.handler;

import cn.hutool.system.UserInfo;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.jwtlogin.auth.common.enums.LoginTypeEnum;
import com.cv.solution.jwtlogin.auth.pojo.dto.UserDetailDTO;
import com.cv.solution.jwtlogin.auth.pojo.query.LoginQuery;
import com.cv.solution.jwtlogin.auth.pojo.result.AuthResult;
import com.cv.solution.jwtlogin.auth.pojo.vo.LoginVO;
import com.cv.solution.jwtlogin.auth.pojo.vo.WebLoginVO;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;


/**
 * @author: xutu
 * @since: 2025/10/10 17:39
 */
@Component
@RequiredArgsConstructor
public class WebLoginHandler extends AbstractLoginHandler<WebLoginVO> {

    private final UserService userService;

    @Override
    public void validateLoginParams(LoginQuery query) {
        if (!StringUtils.hasText(query.getUsername()) || !StringUtils.hasText(query.getPassword())) {
            throw new BizException("用户名或密码不能为空");
        }
    }

    @Override
    public UserDetailDTO authenticate(LoginQuery query) {
        UserDetailDTO user = userService.findByUsername(query.getUsername());
        if (user == null || !user.getPassword().equals(query.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        return user;
    }

    @Override
    protected AuthResult<LoginVO> buildResult(UserInfo user, String token) {
        return new LoginVO(user.getId(), user.getUsername(), token);
    }

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.WEB;
    }
}


