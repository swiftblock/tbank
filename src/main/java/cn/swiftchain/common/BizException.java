package cn.swiftchain.common;

import cn.swiftchain.api.vo.enums.ResponseCode;
import lombok.Data;

@Data
public class BizException extends RuntimeException {
    private ResponseCode code;

    public BizException(ResponseCode code) {
        this.code = code;
    }

    public BizException(ResponseCode code, Throwable e) {
        super(e);
        this.code = code;
    }

    public BizException(String msg) {
        super(msg);
        this.code = ResponseCode.FAILURE;
    }

    public BizException(String msg, Throwable e) {
        super(msg, e);
        this.code = ResponseCode.FAILURE;
    }

    // ~~~ 重写方法

    /**
     * @see java.lang.Throwable#toString()
     */
    @Override public final String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return s + ": " + code.getCode() + "[" + message + "]。";
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override public final String getMessage() {
        return code.getMessage() + "[" + code + "]";
    }

}
