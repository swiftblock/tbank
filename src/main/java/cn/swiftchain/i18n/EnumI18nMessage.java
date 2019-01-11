package cn.swiftchain.i18n;

/**
 * 用于枚举的国际化信息处理接口
 *
 * @author baizhengwen
 * @date 2019-01-09
 */
public interface EnumI18nMessage extends I18nMessage {

    @Override
    default String getMessage() {
        Enum e = (Enum) this;
        return (e.getClass().getName() + '.' + e.name());
    }

}
