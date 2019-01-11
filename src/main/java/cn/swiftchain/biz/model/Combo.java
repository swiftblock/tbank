package cn.swiftchain.biz.model;

import cn.swiftchain.biz.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Combo {
    private Long id;//
    private BigDecimal freezeAmount;//   租借数量
    private Long freezeInterval; //   冻结天数
    private BigDecimal payAmount;//支付金额(以TRX单位，小数点后两位)
    private CurrencyEnum payCurrency; //支付币种(目前写死为"TRX")
    private BigDecimal resourceAmount;//资源数量(以ENERGY为单位)
}