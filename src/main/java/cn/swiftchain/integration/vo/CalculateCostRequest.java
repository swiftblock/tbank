package cn.swiftchain.integration.vo;

import lombok.Data;

@Data
public class CalculateCostRequest {
    //租借天数
    private long loan_days;

    //Trx冻结数量
    private String freeze_amount;

    //支付数量
    private String payment_amount;

    //支付币种
    private String payment_coin_type;
}
