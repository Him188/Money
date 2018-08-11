package money.utils;

/**
 * @author Him188moe @ Money Project
 */
public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(String key) {
        super(key);
    }
}
