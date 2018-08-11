package money.utils;

import cn.nukkit.utils.ConfigSection;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * @author Him188moe @ Money Project
 */
public class Translator {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private ConfigSection language;

    public void load(ConfigSection rootSection) {
        this.language = rootSection;
    }

    /**
     * Translate
     *
     * @param key  key
     * @param args key-value 形式
     */
    public String translate(String key, Object... args) throws LanguageNotFoundException, IllegalArgumentException {
        if (!this.language.containsKey(key)) {
            throw new LanguageNotFoundException(key);
        }

        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("args");
        }

        return translate0(this.language.getString(key), args);
    }

    private static String translate0(String message, Object... args) throws IllegalArgumentException, NullPointerException {
        int i = 0;
        String lastKey = null;
        for (Object arg : args) {
            if ((i++ % 2) == 0) {
                if (!(arg instanceof String)) {
                    throw new IllegalArgumentException("args[" + (i - 1) + "]");
                }
                lastKey = (String) arg;
            } else {
                if (lastKey == null) {
                    throw new NullPointerException();
                }
                Objects.requireNonNull(arg, "args[" + (i - 1) + "]");
                message = message.replace("$" + lastKey + "$", smartToString(arg));
            }
        }
        return message;
    }

    private static String smartToString(Object value) {
        if (value instanceof Double || value instanceof Float) {
            return DECIMAL_FORMAT.format(value);
        }

        return String.valueOf(value);
    }
}
