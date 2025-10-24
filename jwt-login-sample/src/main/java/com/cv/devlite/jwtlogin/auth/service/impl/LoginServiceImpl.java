package com.cv.devlite.jwtlogin.auth.service.impl;

import com.cv.devlite.jwtlogin.auth.common.factory.LoginHandlerFactory;
import com.cv.devlite.jwtlogin.auth.common.handler.AbstractLoginHandler;
import com.cv.devlite.jwtlogin.auth.pojo.query.LoginQuery;
import com.cv.devlite.jwtlogin.auth.pojo.vo.LoginVO;
import com.cv.devlite.jwtlogin.auth.service.ILoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xutu
 * @since: 2025/10/14 15:47
 */
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginHandlerFactory handlerFactory;

    public LoginVO login(LoginQuery query) {
        AbstractLoginHandler<LoginQuery, LoginVO> handler = handlerFactory.getHandler(query.getLoginType());
        return handler.login(query);
    }

}
