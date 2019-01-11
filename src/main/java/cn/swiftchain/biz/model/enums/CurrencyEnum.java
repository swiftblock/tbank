package cn.swiftchain.biz.model.enums;

import org.apache.commons.lang3.StringUtils;

public enum CurrencyEnum {
    TRX("TRX", "TRON代币"),
    VENA("VENA", "VENA代币");

    private String code;
    private String desc;

    CurrencyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CurrencyEnum getBycode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (CurrencyEnum item : values()) {
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
