package cn.swiftchain.i18n;

import lombok.Data;

/**
 * @author baizhengwen
 * @date 2019-01-09
 */
@Data
public class I18nMessages {

    public static I18nMessage of(String message) {
        return new CommonI18nMessage(message);
    }

    public static I18nMessage of(String message, Object[] params) {
        return new CommonI18nMessage(message, params);
    }

    @Data
    static class CommonI18nMessage implements I18nMessage {
        private String message;
        private Object[] params;

        public CommonI18nMessage() {
        }

        public CommonI18nMessage(String message) {
            this.message = message;
        }

        public CommonI18nMessage(String message, Object[] params) {
            this(message);
            this.params = params;
        }
    }
}
