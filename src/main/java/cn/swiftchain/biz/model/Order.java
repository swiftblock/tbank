package cn.swiftchain.biz.model;

import cn.swiftchain.biz.model.enums.CurrencyEnum;
import cn.swiftchain.biz.model.enums.OrderStatusEnum;
import cn.swiftchain.biz.model.enums.ResourceTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Order {
    //订单编号
    String orderNo;

    //用户地址
    String userAddress;

    //购买能源类型
    ResourceTypeEnum resourceType;

    //购买能源数量
    BigDecimal resourceAmount;

    //冻结数量
    BigDecimal freezeAmount;

    //冻结时间
    Long freezeInterval;

    //冻结信息
    String freezeMsg;

    //费用
    BigDecimal fee;

    //费率
    BigDecimal feeRate;

    //订单状态
    OrderStatusEnum status;

    //交易哈希
    String txHash;

    //交易内容
    String rawTx;

    //支付币种
    CurrencyEnum currency;

    //支付数量
    BigDecimal payAmount;

    //支付的区块时间
    Date payBlockTime;

    //报价编号
    String offerNo;

    //报价起始时间
    Date offerStartTime;

    //报价截止时间
    Date offerEndTime;

    //抵押交易哈希
    String resourceTxHash;

    //交易创建时间
    Date gmtCreate;
}
