package cn.swiftchain.biz.model;

import cn.swiftchain.biz.model.enums.OfferStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Offer {
    private String offerNo;//   报价编号
    private OfferStatusEnum status;//   状态码
    private BigDecimal priceTrx;//   TRX单价
    private BigDecimal priceVena;//   VENA单价
    private Date startTime;//   报价起始时间
    private Date endTime;//   报价截止时间
}

