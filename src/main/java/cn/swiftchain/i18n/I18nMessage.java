package cn.swiftchain.i18n;

/**
 * @author baizhengwen
 * @date 2019-01-09
 */
public interface I18nMessage {

    String getMessage();

    default Object[] getParams() {
        return null;
    }
}
