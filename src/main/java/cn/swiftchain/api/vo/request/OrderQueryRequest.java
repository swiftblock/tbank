package cn.swiftchain.api.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrderQueryRequest {
    //用户地址
    @NotBlank
    private String userAddress;

    //起始订单编号
    private String startOrderNo;

    //查询条数
    private Long count;

    //状态(ALL:全部; WAIT:待支付;PAID:已支付)
    @NotBlank
    private String status;

    //签名
    private String sign;
}
