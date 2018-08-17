package money;

import cn.nukkit.Player;
import money.event.bank.BankChangeEvent;
import money.event.bank.BankDecreaseEvent;
import money.event.bank.BankIncreaseEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;

/**
 * API with descriptions. <br>
 * Implemented by {@link Money} <br>
 * Use {@link #getInstance()} to call APIs.
 *
 * @author Him188 @ Project
 * @version 1.0
 * @see Money
 * @since 1.0
 */
public interface MoneyAPI extends MoneyAPIDeprecated {
    /**
     * Gets instance of {@link MoneyAPI}
     *
     * @see Money#getInstance()
     */
    static MoneyAPI getInstance() {
        return Money.getInstance();
    }

    /**
     * 获取第一种货币 String 值 <br>
     * Gets config value of the first currency unit, like "Coin"
     *
     * @return name of the first currency unit
     */
    String getCurrency1();

    /**
     * 获取第二种货币 String 值 <br>
     * Gets config value of the first currency unit, like "Point"
     *
     * @return name of the first currency unit
     */
    String getCurrency2();

    /**
     * 获取第一/二种货币 String 值 <br>
     * Gets config value of the first/second currency unit, like "Coin"/"Point"
     *
     * @param type 类型 <br>
     *             type
     *
     * @return 第一/二种货币 String 值 <br>
     * config value of the first/second currency unit
     */
    String getCurrency(CurrencyType type);

    /**
     * 检查第二种货币是否已开启 <br>
     * Check if the second currency is enabled
     *
     * @return whether the second currency is enabled
     *
     * @see Money
     */
    boolean isCurrency2Enabled();

    /**
     * 获取一个玩家的货币数量. 如果玩家不存在, 将返回 0 <br>
     * Gets one's currency1 amount. If <code>player</code> is valid, it returns 0.
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type
     *
     * @return currency1 balance in float
     */
    float getMoney(String player, CurrencyType type);

    /**
     * 获取一个玩家的货币数量. 如果玩家不存在, 将返回 0 <br>
     * Gets one's currency1 amount. If <code>player</code> is valid, it returns 0.
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type
     *
     * @return currency1 balance in float
     */
    float getMoney(Player player, CurrencyType type);


    /**
     * 获取一个玩家的货币1数量. 如果玩家不存在, 将返回 0 <br>
     * Gets one's currency1 amount. If <code>player</code> is invalid, it returns 0.
     *
     * @param player 玩家名
     *
     * @return currency1 balance in float
     */
    float getMoney(String player);

    /**
     * 获取一个玩家的货币1数量. 如果玩家不存在, 将返回 0 <br>
     * Gets one's currency1 amount. If <code>player</code> is invalid, it returns 0.
     *
     * @param player 玩家名
     *
     * @return currency1 balance in float
     */
    float getMoney(Player player);

    /**
     * 设置一个玩家的货币数量. 会触发 {@link MoneyChangeEvent} <br>
     * Modify one's currency amount. It will trigger {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param money  数量 <br>
     *               amount
     * @param type   货币类型 <br>
     *               currency type
     *
     * @return whether succeed or not.
     */
    boolean setMoney(String player, float money, CurrencyType type);

    /**
     * 设置一个玩家的货币数量. 会触发 {@link MoneyChangeEvent} <br>
     * Modify one's currency amount. It will trigger {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param money  数量 <br>
     *               amount
     * @param type   货币类型 <br>
     *               currency type
     *
     * @return whether succeed or not.
     */
    boolean setMoney(Player player, float money, CurrencyType type);


    /**
     * 设置一个玩家的货币1 数量. 会触发 {@link MoneyChangeEvent} <br>
     * Modify one's currency1 amount. It will trigger {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br> player's name
     * @param money  数量 <br> amount
     *
     * @return whether succeed or not.
     */
    boolean setMoney(String player, float money);

    /**
     * 设置一个玩家的货币1 数量. 会触发 {@link MoneyChangeEvent} <br>
     * Modify one's currency1 amount. It will trigger {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br> player's name
     * @param money  数量 <br> amount
     *
     * @return whether succeed or not.
     */
    boolean setMoney(Player player, float money);

    /**
     * 获取一个玩家的银行储蓄. 如果玩家不存在, 将返回 0 <br>
     * Gets one's bank account balance. If <code>player</code> is invalid, it returns 0
     *
     * @param player 玩家名 <br>
     *               player's name
     *
     * @return bank account balance in float.
     */
    float getBank(Player player);

    /**
     * 获取一个玩家的银行储蓄. 如果玩家不存在, 将返回 0 <br>
     * Gets one's bank account balance. If <code>player</code> is invalid, it returns 0
     *
     * @param player 玩家名 <br>
     *               player's name
     *
     * @return bank account balance in float.
     */
    float getBank(String player);


    /**
     * 设置一个玩家的银行储蓄. 会触发 {@link BankChangeEvent} <br>
     * Modify one's bank account balance. It will trigger {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param money  数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     *
     * @see Money#setBank(String, float)
     * @see Money#setBank(Player, float)
     */
    boolean setBank(String player, float money);

    /**
     * 设置一个玩家的银行储蓄. 会触发 {@link BankChangeEvent} <br>
     * Modify one's bank account balance. It will trigger {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param money  数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     *
     * @see Money#setBank(String, float)
     * @see Money#setBank(Player, float)
     */
    boolean setBank(Player player, float money);


    /**
     * 增加一个玩家的货币1数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Increase one's currency1 amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #reduceMoney(Player, float)}
     *
     * @return whether succeed or not.
     *
     * @since 1.3.0
     */
    boolean addMoney(String player, float amount);

    /**
     * 增加一个玩家的货币1数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Increase one's currency1 amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #reduceMoney(Player, float)}
     *
     * @return whether succeed or not.
     *
     * @since 1.3.0
     */
    boolean addMoney(Player player, float amount);


    /**
     * 增加一个玩家的货币数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent} <br>
     * Increase one's currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float, CurrencyType)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #reduceMoney(Player, float)}
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type. In old api, true represents {@link CurrencyType#SECOND}, false represents {@link CurrencyType#FIRST}
     *
     * @return whether succeed or not.
     *
     * @since 1.3.0
     */
    boolean addMoney(String player, float amount, CurrencyType type);

    /**
     * 增加一个玩家的货币数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent} <br>
     * Increase one's currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float, CurrencyType)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #reduceMoney(Player, float)}
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type. In old api, true represents {@link CurrencyType#SECOND}, false represents {@link CurrencyType#FIRST}
     *
     * @return whether succeed or not.
     *
     * @since 1.3.0
     */
    boolean addMoney(Player player, float amount, CurrencyType type);


    /**
     * 减少一个玩家的货币1数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Decrease one's currency1 amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #addMoney(Player, float)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #addMoney(Player, float)}
     *
     * @return whether succeed or not.
     */
    boolean reduceMoney(String player, float amount);

    /**
     * 减少一个玩家的货币1数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Decrease one's currency1 amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #addMoney(Player, float)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #addMoney(Player, float)}
     *
     * @return whether succeed or not.
     */
    boolean reduceMoney(Player player, float amount);


    /**
     * 减少一个玩家的货币数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Decrease one's currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #addMoney(Player, float, CurrencyType)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #addMoney(Player, float, CurrencyType)}
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type. In old api, true represents {@link CurrencyType#SECOND}, false represents {@link CurrencyType#FIRST}
     *
     * @return whether succeed or not.
     */
    boolean reduceMoney(String player, float amount, CurrencyType type);

    /**
     * 减少一个玩家的货币数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
     * Decrease one's currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量. 可以负数 (负数时会调用 {@link #addMoney(Player, float, CurrencyType)}) <br>
     *               amount. Can be negative. When it is negative, this method will call {@link #addMoney(Player, float, CurrencyType)}
     * @param type   货币类型. true: 货币2, false: 货币1 <br>
     *               currency type. In old api, true represents {@link CurrencyType#SECOND}, false represents {@link CurrencyType#FIRST}
     *
     * @return whether succeed or not.
     */
    boolean reduceMoney(Player player, float amount, CurrencyType type);


    /**
     * 增加一个玩家的银行储蓄. 会触发 {@link BankIncreaseEvent} 随后触发 {@link BankChangeEvent}
     * Increase one's bank account balance. It will trigger {@link BankIncreaseEvent} and {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     */
    boolean addBank(String player, float amount);

    /**
     * 增加一个玩家的银行储蓄. 会触发 {@link BankIncreaseEvent} 随后触发 {@link BankChangeEvent}
     * Increase one's bank account balance. It will trigger {@link BankIncreaseEvent} and {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     */
    boolean addBank(Player player, float amount);


    /**
     * 减少一个玩家的银行储蓄. 会触发 {@link BankDecreaseEvent} 随后触发 {@link BankChangeEvent}
     * Decrease one's bank account balance. It will trigger {@link BankDecreaseEvent} and {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     */
    boolean reduceBank(String player, float amount);

    /**
     * 减少一个玩家的银行储蓄. 会触发 {@link BankDecreaseEvent} 随后触发 {@link BankChangeEvent}
     * Decrease one's bank account balance. It will trigger {@link BankDecreaseEvent} and {@link BankChangeEvent}
     *
     * @param player 玩家名 <br>
     *               player's name
     * @param amount 数量 <br>
     *               amount
     *
     * @return whether succeed or not.
     */
    boolean reduceBank(Player player, float amount);


    /**
     * 创建一个玩家账户 <br>
     * Creates a initial account.
     *
     * @param player player name
     * @param money1 {@link CurrencyType#FIRST}
     * @param money2 {@link CurrencyType#SECOND}
     * @param bank   bank
     *
     * @return whether succeed or not
     *
     * @since 3.2
     */
    boolean createAccount(String player, float money1, float money2, float bank);


    /* Deprecated */
}

