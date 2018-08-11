package money.utils;

/**
 * @author Him188moe @ Money Project
 */
public enum LanguageType {
    CHS,
    ENG,
    CHT,
    ;

    public static LanguageType DEFAULT_LANGUAGE = ENG;

    public static LanguageType fromString(String string) throws IllegalArgumentException {
        return LanguageType.valueOf(string.toUpperCase());
    }
}
