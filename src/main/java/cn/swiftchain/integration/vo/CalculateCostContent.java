package cn.swiftchain.integration.vo;

import lombok.Data;

@Data
public class CalculateCostContent {
    //需要的trx数量
    private String trx_amount;

    //需要的vena数量
    private String vena_amount;

    //需要冻结的trx数量
    private String freeze_amount;

    //使用trx支付的价格
    private String trx_price;

    //使用vena支付的价格
    private String vena_price;

    //报价编号
    private String price_id;

    //报价生效时间
    private Long effective_time;

    private Long expiration_time;
}
