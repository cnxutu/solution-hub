package com.cv.i18n.interceptor;

import com.cv.i18n.context.I18nContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Slf4j
public class I18nLocaleInterceptor implements HandlerInterceptor {

    public static final String LANGUAGE_HEADER = "Accept-Language";
    public static final String I18N_LANGUAGE_HEADER = "X-Language";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String xLanguage = request.getHeader(I18N_LANGUAGE_HEADER);
        String acceptLanguage = request.getHeader(LANGUAGE_HEADER);
        Locale locale = resolveLocale(request);
        
        log.debug("[I18nLocaleInterceptor] X-Language: {}, Accept-Language: {}, resolved locale: {}", 
                xLanguage, acceptLanguage, locale);
        
        I18nContextHolder.setLocale(locale);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        I18nContextHolder.clear();
    }

    private Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader(I18N_LANGUAGE_HEADER);
        if (language == null || language.trim().isEmpty()) {
            language = request.getHeader(LANGUAGE_HEADER);
        }
        return I18nContextHolder.parseLocale(language);
    }
}