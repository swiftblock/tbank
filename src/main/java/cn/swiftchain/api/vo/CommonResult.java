package cn.swiftchain.api.vo;

import cn.swiftchain.api.vo.enums.ResponseCode;
import cn.swiftchain.i18n.EnumI18nMessage;
import cn.swiftchain.i18n.I18nMessage;
import cn.swiftchain.i18n.I18nMessages;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CommonResult<T> {

    String code;
    I18nMessage msg;

    T data;

    public CommonResult() {
        code = ResponseCode.SUCCESS.getCode();
        msg = ResponseCode.SUCCESS;
    }

    public boolean success() {
        return StringUtils.equals(ResponseCode.SUCCESS.getCode(), code);
    }

    public static CommonResult failure(String msg) {
        CommonResult result = new CommonResult();
        result.setCode(ResponseCode.FAILURE.getCode());
        result.setMsg(I18nMessages.of(msg));
        return result;
    }

    public static CommonResult failure(EnumI18nMessage msg) {
        CommonResult result = new CommonResult();
        result.setCode(ResponseCode.FAILURE.getCode());
        result.setMsg(msg);
        return result;
    }
}
