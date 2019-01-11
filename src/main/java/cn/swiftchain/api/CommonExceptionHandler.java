package cn.swiftchain.api;

import cn.swiftchain.api.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author baizhengwen
 * @date 2018-11-08
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class CommonExceptionHandler {


    @ExceptionHandler
    public Object handle(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        String errorMsg = fieldError.getField() + fieldError.getDefaultMessage();
        return CommonResult.failure(errorMsg);
    }

    @ExceptionHandler
    public Object handle(RuntimeException e) {
        log.error(e.getMessage(), e);
        return CommonResult.failure(e.getMessage());
    }

    @ExceptionHandler
    public Object handle(Exception e) {
        log.error(e.getMessage(), e);
        return CommonResult.failure(e.getMessage());
    }
}
