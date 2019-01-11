package cn.swiftchain.integration.vo;

import lombok.Data;

@Data
public class QueryPriceContent {
    //状态
    private String status;

    //Trx价格
    private String trx_price;

    //Vena价格
    private String vena_price;

    //报价生效时间
    private Long effective_time;

    //报价结束时间
    private Long expiration_time;
}
