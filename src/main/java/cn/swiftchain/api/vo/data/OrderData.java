package cn.swiftchain.api.vo.data;

import cn.swiftchain.biz.model.enums.OrderStatusEnum;
import cn.swiftchain.i18n.annotation.I18NEnum;
import lombok.Data;

@Data
public class OrderData {
    //订单编号
    private String orderNo;

    //订单状态(英文缩写)
    @I18NEnum(OrderStatusEnum.class)
    private String status;

    //订单状态字典(中文)
    private String statusDict;

    //用户地址
    private String userAddress;

    //购买能源类型
    private String resourceType;

    //购买能源数量
    private String resourceAmount;

    //冻结数量
    private String freezeAmount;

    //冻结时间
    private Long freezeInterval;

    //交易hash
    private String txHash;

    //交易内容
    private String rawTx;

    //支付币种(目前写死TRX)
    private String currency;

    //支付金额(TRX为单位)
    private String payAmount;

    //报价编号
    private String offerNo;

    //是否失效
    private Boolean isValid;

    //剩余秒数
    private Long restTime;

    //创建时间
    private Long gmtCreate;
}
