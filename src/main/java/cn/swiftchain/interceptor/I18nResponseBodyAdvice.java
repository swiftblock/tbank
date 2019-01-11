package cn.swiftchain.interceptor;

import cn.swiftchain.i18n.I18nMessageValueFilter;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author baizhengwen
 * @date 2019-01-09
 */
@ControllerAdvice
@Slf4j
public class I18nResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Autowired
    private I18nMessageValueFilter i18nMessageValueFilter;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String result = JSON.toJSONString(o, i18nMessageValueFilter);
        return JSON.parseObject(result);
    }
}