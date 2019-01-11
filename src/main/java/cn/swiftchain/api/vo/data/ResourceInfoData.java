package cn.swiftchain.api.vo.data;

import lombok.Data;

import java.util.List;

@Data
public class ResourceInfoData {
    //可用数量
    private Long availableFunds;

    //报价编号
    private String offerNo;

    //资金冻结单价，冻结1个TRX资金一小时需要多少trx
    private String trxFreezePrice;

    //资金冻结单价，冻结1个TRX资金一小时需要多少vena
    private String venaFreezePrice;

    //资源单价，冻结一个Trx预估可以获取多少资源
    private String trxResourcePrice;

    //剩余可用时间
    private Long restTime;

    //最小TRX数量
    private Long minTrxFreezeAmount;

    //最大TRX数量
    private Long maxTrxFreezeAmount;

    //最小冻结时间(天为单位)
    private Long minTrxFreezeInterval;

    //最大冻结时间(天为单位)
    private Long maxTrxFreezeInterval;

    //套餐列表
    private List<PackData> packageList;

    //收款地址
    private String receiveAddress;
}
