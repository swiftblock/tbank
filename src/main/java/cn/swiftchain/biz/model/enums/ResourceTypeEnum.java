package cn.swiftchain.biz.model.enums;

import org.apache.commons.lang3.StringUtils;

public enum ResourceTypeEnum {
    ENERGY("ENERGY", "能量"),
    BANDWIDTH("BANDWIDTH", "贷款");

    private String code;
    private String desc;

    ResourceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ResourceTypeEnum getBycode(String code) {
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        for (ResourceTypeEnum item : values()) {
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
