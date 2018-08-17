package money;

import cn.nukkit.Player;
import money.event.bank.BankChangeEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyIncreaseEvent;

/**
 * Collections of deprecated APIs. <br>
 * For compatibility, we will not delete these APIs yet. <br>
 * But plugins should update to use NEW APIs.
 *
 * @author Him188moe @ Money Project
 * @see MoneyAPI Use instead
 * @since 3.3
 */
public interface MoneyAPIDeprecated {
    @Deprecated
    String getMoneyUnit1();

    @Deprecated
    String getMonetaryUnit1();

    @Deprecated
    String getMoneyUnit2();

    @Deprecated
    String getMonetaryUnit2();

    @Deprecated
    String getMoneyUnit(CurrencyType type);

    @Deprecated
    String getMonetaryUnit(CurrencyType type);

    @Deprecated
    String getMoneyUnit(boolean unit);

    @Deprecated
    String getMonetaryUnit(boolean unit);

    @Deprecated
    boolean isMoneyUnit2Enabled();

    @Deprecated
    float getMoney(String player, boolean type);

    @Deprecated
    float getMoney(Player player, boolean type);

    @Deprecated
    boolean setMoney(String player, float money, boolean type);

    @Deprecated
    boolean setMoney(Player player, float money, boolean type);

    @Deprecated
    boolean addMoney(Player player, float amount, boolean type);

    @Deprecated
    boolean addMoney(String player, float amount, boolean type);

    @Deprecated
    boolean reduceMoney(Player player, float amount, boolean type);

    @Deprecated
    boolean reduceMoney(String player, float amount, boolean type);

    /**
     * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyChangeEvent}
     * Modify all players' currency amount. It will trigger {@link MoneyChangeEvent}
     *
     * @param amount 数量 <br>
     *               amount
     *
     * @return 成功操作的玩家数 <br>
     * number of successful operations
     */
    @Deprecated
    int setAllMoney(float amount);

    @Deprecated
    int setAllMoney(float amount, CurrencyType type);

    /**
     * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyIncreaseEvent} {@link MoneyChangeEvent}
     * Increase all players' currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param amount 数量 <br>
     *               amount
     *
     * @return 成功操作的玩家数 <br>
     * number of successful operations
     */
    @Deprecated
    int addAllMoney(float amount);

    @Deprecated
    int addAllMoney(float amount, CurrencyType type);

    /**
     * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyIncreaseEvent} {@link MoneyChangeEvent}
     * Decrease all players' currency amount. It will trigger {@link MoneyIncreaseEvent} and {@link MoneyChangeEvent}
     *
     * @param amount 数量 <br>
     *               amount
     *
     * @return 成功操作的玩家数 <br>
     * number of successful operations
     */
    @Deprecated
    int reduceAllMoney(float amount);

    @Deprecated
    int reduceAllMoney(float amount, CurrencyType type);

    /**
     * 设置全服玩家(数据库中所有已记录的玩家)的银行余额. 会触发 {@link BankChangeEvent}
     * Modify all players' bank account balance. It will trigger {@link BankChangeEvent}
     *
     * @param amount 余额 <br>
     *               amount
     *
     * @return 成功操作的玩家数 <br>
     * number of successful operations
     */
    @Deprecated
    int setAllBank(float amount);
}
