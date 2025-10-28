package com.cv.solution.jwtlogin.auth.common.factory;

import com.cv.boot.common.exception.BizException;
import com.cv.solution.jwtlogin.auth.common.enums.LoginTypeEnum;
import com.cv.solution.jwtlogin.auth.common.handler.AbstractLoginHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LoginHandlerFactory 类简要描述
 *
 * @author xutu
 * @date 2025-10-11 11:15:42
 */
/**
 * 登录处理器工厂，根据 loginType 返回对应 Handler
 */
@Component
@RequiredArgsConstructor
public class LoginHandlerFactory {

    private final Map<Integer, AbstractLoginHandler<?>> handlerMap = new HashMap<>();

    /**
     * 构造函数自动注册所有 Handler
     */
    public LoginHandlerFactory(List<AbstractLoginHandler<?>> handlers) {
        handlers.forEach(handler -> {
            LoginTypeEnum type = handler.getLoginType();
            if (handlerMap.containsKey(type.getCode())) {
                throw new IllegalStateException("重复的登录类型 Handler: " + type);
            }
            handlerMap.put(type.getCode(), handler);
        });
    }

    /**
     * 根据 loginType 获取对应 Handler
     */
    @SuppressWarnings("unchecked")
    public <R> AbstractLoginHandler<R> getHandler(Integer loginType) {
        AbstractLoginHandler<?> handler = handlerMap.get(loginType);
        if (handler == null) {
            throw new BizException("不支持的登录类型: " + loginType);
        }
        return (AbstractLoginHandler<R>) handler;
    }
}

