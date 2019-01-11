package cn.swiftchain.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * @author baizhengwen
 * @create 2019-01-04
 */
@Component
public class LocaleInterceptor extends HandlerInterceptorAdapter {

    private static final String LANG_SEPARATOR = "_";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String lang = request.getParameter("lang");
        if (StringUtils.isNoneBlank(lang)) {
            String language = StringUtils.substringBefore(lang, LANG_SEPARATOR);
            String country = StringUtils.substringAfter(lang, LANG_SEPARATOR);
            Locale locale = new Locale(language, country);

            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            localeResolver.setLocale(request, response, locale);
        }

        return super.preHandle(request, response, handler);
    }
}
