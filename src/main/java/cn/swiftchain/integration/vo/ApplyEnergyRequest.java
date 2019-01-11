package cn.swiftchain.integration.vo;

import lombok.Data;

@Data
public class ApplyEnergyRequest {
    //交易地址
    private String payment_address;

    //交易哈希
    private String transaction_hash;

    //支付币种
    private String payment_coin_type;

    //目标冻结数量
    private Long freeze_amount;

    //租借天数
    private Long loan_days;

    //备注
    private String remark;
}
