package cn.swiftchain.biz.model.enums;

import org.apache.commons.lang3.StringUtils;

public enum OrderStatusEnum {
    WAIT("WAIT", "待支付"),
    PAID("PAID", "已支付"),
    APPLIED("APPLIED", "已申请资源"),
    DONE("DONE", "资源已发放"),
    TIMEOUT("TIMEOUT", "支付超时"),
    REMOVED("REMOVED", "已删除"),
    FAILED("FAILED", "冻结失败"),
    REFUND("REFUND", "已退款"),
    ;

    private String code;
    private String desc;

    OrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum getBycode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (OrderStatusEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
