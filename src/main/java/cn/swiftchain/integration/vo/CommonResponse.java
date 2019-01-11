package cn.swiftchain.integration.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CommonResponse<T> {
    private static final String SUCCESS = "00";

    private String response_code;

    private String response_msg;

    T content;

    public boolean isSuccess() {
        return StringUtils.equals(SUCCESS, response_code);
    }

}
