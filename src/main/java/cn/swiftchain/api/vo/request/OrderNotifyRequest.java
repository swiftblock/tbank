package cn.swiftchain.api.vo.request;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class OrderNotifyRequest {
    private static final String SUCCESS = "00";

    //冻结编号
    @NotBlank
    private String freeze_id;

    //抵押hash
    private String freeze_hash;

    //支付hash
    @NotBlank
    private String transaction_hash;

    //冻结结果
    @NotBlank
    private String response_code;

    //冻结消息
    private String response_msg;

    public boolean success () {
        return StringUtils.equals(SUCCESS, response_code);
    }
}
