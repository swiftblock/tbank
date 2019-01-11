package cn.swiftchain.biz.model.enums;

import org.apache.commons.lang3.StringUtils;

public enum OfferStatusEnum {
    VALID("VALID", "已生效"),
    INVALID("INVALID", "已失效");

    private String code;
    private String desc;

    OfferStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OfferStatusEnum getBycode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (OfferStatusEnum item : values()) {
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
