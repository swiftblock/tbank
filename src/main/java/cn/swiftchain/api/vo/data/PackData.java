package cn.swiftchain.api.vo.data;

import lombok.Data;

@Data
public class PackData {
    //租借数量(最小单位)
    private Long freezeAmount;

    //租借时间
    private Long freezeInterval;

    //支付金额(以TRX单位，小数点后两位)
    private String payAmount;

    //支付币种(目前写死为"TRX")
    private String payCurrency;

    //资源数量(以ENERGY为单位)
    private String resourceAmount;
}
