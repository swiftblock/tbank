package cn.swiftchain.api.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrderDeleteRequest {
    //用户地址
    @NotBlank
    private String userAddress;

    //起始订单编号
    @NotBlank
    private String orderNo;

    //签名
    private String sign;
}
