package com.cv.i18n.filter;

import com.cv.i18n.context.I18nContextHolder;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

public class I18nLocaleFilter extends OncePerRequestFilter {

    public static final String LANGUAGE_HEADER = "Accept-Language";
    public static final String I18N_LANGUAGE_HEADER = "X-Language";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Locale locale = resolveLocale(request);
        I18nContextHolder.setLocale(locale);
        try {
            filterChain.doFilter(request, response);
        } finally {
            I18nContextHolder.clear();
        }
    }

    private Locale resolveLocale(HttpServletRequest request) {
        String language = request.getHeader(I18N_LANGUAGE_HEADER);
        if (language == null || language.trim().isEmpty()) {
            language = request.getHeader(LANGUAGE_HEADER);
        }
        return I18nContextHolder.parseLocale(language);
    }
}
