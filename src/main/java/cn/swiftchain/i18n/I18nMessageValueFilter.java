package cn.swiftchain.i18n;

import cn.swiftchain.i18n.annotation.I18NEnum;
import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Locale;

/**
 * @author baizhengwen
 * @date 2019-01-09
 */
@Slf4j
@Component
public class I18nMessageValueFilter implements ValueFilter {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Object process(Object object, String name, Object value) {
        if (value instanceof I18nMessage) {
            I18nMessage message = (I18nMessage) value;
            return getMessage(message.getMessage(), message.getParams());
        }

        try {
            Field field = object.getClass().getDeclaredField(name);
            I18NEnum annotation = field.getAnnotation(I18NEnum.class);
            if (null != annotation) {
                return getMessage(annotation.value().getName() + '.' + value);
            }
        } catch (NoSuchFieldException e) {
            log.error(e.getMessage(), e);
        }

        return value;
    }

    private String getMessage(String key, Object... params) {
        return messageSource.getMessage(key, params, key, getLocale());
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
