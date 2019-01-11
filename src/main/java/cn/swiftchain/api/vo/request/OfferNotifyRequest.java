package cn.swiftchain.api.vo.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OfferNotifyRequest {
    //被影响的报价编号
    @NotBlank
    private String disabled_price_id;

    //新报价编号
    private String new_price_id;

    //新报价编号过期时间
    private Long price_expiration_time;

    //新报价起始时间
    private Long price_effective_time;
}
