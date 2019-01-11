package cn.swiftchain.api.vo.enums;

import org.apache.commons.lang3.StringUtils;

public enum OrderQueryStatusEnum {
    WAIT("WAIT", "待支付"),
    PAID("PAID", "已支付"),
    ALL("ALL", "全部");

    private String code;
    private String desc;

    OrderQueryStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderQueryStatusEnum getBycode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (OrderQueryStatusEnum item : values()) {
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
