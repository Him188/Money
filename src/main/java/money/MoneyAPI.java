package money;

import cn.nukkit.Player;
import money.event.bank.BankChangeEvent;
import money.event.bank.BankDecreaseEvent;
import money.event.bank.BankIncreaseEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;

import java.util.Set;

/**
 * Money API
 *
 * @author Him188 @ Money Project
 * @see Money
 */
public interface MoneyAPI {
	/**
	 * @see Money#getInstance()
	 */
	static Money getInstance() {
		return Money.getInstance();
	}


	/**
	 * 获取第一种货币 String 值
	 *
	 * @return String
	 */
	@Deprecated
	String getMoneyUnit1();

	@Deprecated
	String getMonetaryUnit1();

	String getCurrency1();


	/**
	 * 获取第二种货币 String 值
	 *
	 * @return String
	 */
	@Deprecated
	String getMoneyUnit2();

	@Deprecated
	String getMonetaryUnit2();

	String getCurrency2();

	/**
	 * 获取第一/二种货币 String 值
	 *
	 * @param type 类型
	 *
	 * @return 第一/二种货币 String 值
	 */
	String getCurrency(CurrencyType type);

	@Deprecated
	String getMoneyUnit(CurrencyType type);

	@Deprecated
	String getMonetaryUnit(CurrencyType type);

	@Deprecated
	String getMoneyUnit(boolean unit);

	@Deprecated
	String getMonetaryUnit(boolean unit);


	/**
	 * 检查第二种货币是否已开启
	 *
	 * @return boolean
	 *
	 * @see Money
	 */
	@Deprecated
	boolean isMoneyUnit2Enabled();

	boolean isCurrency2Enabled();


	/**
	 * 获取一个玩家的货币数量. 如果玩家不存在, 将返回 0
	 *
	 * @param player 玩家名
	 * @param type   货币类型. true: 货币2, false: 货币1
	 *
	 * @return float
	 */
	@Deprecated
	float getMoney(String player, boolean type);

	@Deprecated
	float getMoney(Player player, boolean type);

	float getMoney(String player, CurrencyType type);

	float getMoney(Player player, CurrencyType type);


	/**
	 * 获取一个玩家的货币1数量. 如果玩家不存在, 将返回 0
	 *
	 * @param player 玩家名
	 *
	 * @return float
	 */
	float getMoney(String player);

	float getMoney(Player player);


	/**
	 * 设置一个玩家的货币数量. 会触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param money  数量
	 * @param type   货币类型
	 */
	@Deprecated
	boolean setMoney(String player, float money, boolean type);

	@Deprecated
	boolean setMoney(Player player, float money, boolean type);

	boolean setMoney(String player, float money, CurrencyType type);

	boolean setMoney(Player player, float money, CurrencyType type);


	/**
	 * 设置一个玩家的货币1 数量. 会触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param money  数量
	 */
	boolean setMoney(String player, float money);

	boolean setMoney(Player player, float money);


	/**
	 * 获取一个玩家的银行储蓄. 如果玩家不存在, 将返回 0
	 *
	 * @param player 玩家名
	 *
	 * @return float
	 */
	float getBank(Player player);

	float getBank(String player);


	/**
	 * 设置一个玩家的银行储蓄. 会触发 {@link BankChangeEvent}
	 *
	 * @param player 玩家名
	 * @param money  数量
	 *
	 * @see Money#setBank(String, float)
	 * @see Money#setBank(Player, float)
	 */
	boolean setBank(String player, float money);

	boolean setBank(Player player, float money);


	/**
	 * 增加一个玩家的货币1数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float)})
	 *
	 * @since Money 1.3.0
	 */
	boolean addMoney(String player, float amount);

	boolean addMoney(Player player, float amount);


	/**
	 * 增加一个玩家的货币数量. 会触发 {@link MoneyIncreaseEvent} 随后触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, float, CurrencyType)})
	 * @param type   货币类型. true: 货币2, false: 货币1
	 *
	 * @since Money 1.3.0
	 */

	boolean addMoney(String player, float amount, CurrencyType type);

	boolean addMoney(Player player, float amount, CurrencyType type);

	@Deprecated
	boolean addMoney(Player player, float amount, boolean type);

	@Deprecated
	boolean addMoney(String player, float amount, boolean type);

	/**
	 * 减少一个玩家的货币1数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量.
	 */
	boolean reduceMoney(String player, float amount);

	boolean reduceMoney(Player player, float amount);


	/**
	 * 减少一个玩家的货币数量. 会触发 {@link MoneyDecreaseEvent} 随后触发 {@link MoneyChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量.
	 * @param type   货币类型. true: 货币2, false: 货币1
	 */
	boolean reduceMoney(String player, float amount, CurrencyType type);

	boolean reduceMoney(Player player, float amount, CurrencyType type);

	@Deprecated
	boolean reduceMoney(Player player, float amount, boolean type);

	@Deprecated
	boolean reduceMoney(String player, float amount, boolean type);


	/**
	 * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyChangeEvent}
	 *
	 * @param amount 数量
	 *
	 * @return 成功操作的玩家数
	 */
	int setAllMoney(float amount);

	int setAllMoney(float amount, CurrencyType type);


	/**
	 * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyIncreaseEvent} {@link MoneyChangeEvent}
	 *
	 * @param amount 数量
	 *
	 * @return 成功操作的玩家数
	 */
	int addAllMoney(float amount);

	int addAllMoney(float amount, CurrencyType type);


	/**
	 * 设置全服玩家(数据库中所有已记录的玩家)的货币数量. 会触发 {@link MoneyIncreaseEvent} {@link MoneyChangeEvent}
	 *
	 * @param amount 数量
	 *
	 * @return 成功操作的玩家数
	 */
	int reduceAllMoney(float amount);

	int reduceAllMoney(float amount, CurrencyType type);


	/**
	 * 增加一个玩家的银行储蓄. 会触发 {@link BankIncreaseEvent} 随后触发 {@link BankChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量
	 */
	boolean addBank(String player, float amount);

	boolean addBank(Player player, float amount);


	/**
	 * 减少一个玩家的银行储蓄. 会触发 {@link BankDecreaseEvent} 随后触发 {@link BankChangeEvent}
	 *
	 * @param player 玩家名
	 * @param amount 数量
	 */
	boolean reduceBank(String player, float amount);

	boolean reduceBank(Player player, float amount);


	/**
	 * 设置全服玩家(数据库中所有已记录的玩家)的银行余额. 会触发 {@link BankChangeEvent}
	 *
	 * @param amount 余额
	 *
	 * @return 成功操作的玩家数
	 */
	int setAllBank(float amount);

	/**
	 * 获取数据库中记录的所有玩家名字
	 *
	 * @return 所有玩家名(可能含有无效名), 可用于遍历等操作
	 */
	Set<String> getPlayers();

	/**
	 * 获取数据库中记录的所有玩家名字并过滤无效名字
	 *
	 * @return 所有玩家名, 可用于遍历等操作
	 */
	Set<String> getPlayersFiltered();
}

