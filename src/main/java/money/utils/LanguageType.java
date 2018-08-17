package money.utils;

import java.util.Locale;

/**
 * Language types.
 *
 * @author Him188moe @ Money Project
 * @version 1.1
 * @since 3.0
 */
public enum LanguageType {
    CHS("简体中文"),
    CHT("繁體中文"),
    ENG("English"),
    ;

    public static final String[] stringValues;

    static {
        stringValues = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].name().toLowerCase();
        }
    }

    private String translationName;

    LanguageType(String translationName) {
        this.translationName = translationName;
    }

    public String getTranslationName() {
        return translationName;
    }

    public static LanguageType fromString(String string) throws IllegalArgumentException {
        return LanguageType.valueOf(string.toUpperCase());
    }

    private static LanguageType type;

    public static LanguageType getDefaultLanguage() {
        if (type != null) {
            return type;
        }

        Locale locale = Locale.getDefault();

        if (locale == Locale.SIMPLIFIED_CHINESE) {
            return type = CHS;
        } else if (locale == Locale.TRADITIONAL_CHINESE) {
            return type = CHT;
        } else {
            return type = ENG;
        }
    }
}
