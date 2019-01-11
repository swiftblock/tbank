package cn.swiftchain.api.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TakeOrderRequest {
    //用户地址
    @NotBlank
    private String userAddress;

    //购买能源类型(写死ENERGY)
    @NotBlank
    private String resourceType;

    //购买能源数量
    @NotBlank
    private String resourceAmount;

    //冻结数量
    @NotNull
    private Long trxFreezeAmount;

    //冻结时间
    @NotNull
    private Long trxFreezeInterval;

    //交易hash
    @NotBlank
    private String txHash;

    //交易内容
    @NotBlank
    private String rawTx;

    //支付币种(目前写死TRX)
    @NotBlank
    private String currency;

    //支付数量(TRX为单位)
    @NotBlank
    private String payAmount;

    //报价编号
    @NotBlank
    private String offerNo;

    //发起时间
    @NotNull
    private Long bizTime;
}
