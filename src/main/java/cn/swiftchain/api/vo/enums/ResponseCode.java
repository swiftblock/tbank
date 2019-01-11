package cn.swiftchain.api.vo.enums;

import cn.swiftchain.i18n.EnumI18nMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author baizhengwen
 * @date 2019-01-09
 */
@Getter
@AllArgsConstructor
public enum ResponseCode implements EnumI18nMessage {
    SUCCESS("000000", "操作成功"),
    OFFER_INVALID("000001", "报价已失效"),
    OFFER_TIMEOUT("000002", "报价已超时"),
    ORDER_CANT_REMOVE("000003", "订单不能被删除"),
    ORDER_REMOVE_FAILED("000004", "订单删除失败"),
    ORDER_NOT_EXIST("000005", "订单不存在"),
    ORDER_STATUS_INVALID("000006", "订单状态不合法"),
    FAILURE("999999", "操作失败");

    private String code;
    private String desc;

}
