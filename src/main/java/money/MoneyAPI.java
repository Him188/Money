package money;

import cn.nukkit.Player;

/**
 * Money API
 *
 * @author Him188 @ Money Project
 * @see Money
 * @since Money 1.0.0
 */
public interface MoneyAPI {
	/**
	 * @return 插件已加载时 {@link Money}; 未开启时 {@code null}
	 *
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
	@Deprecated
	String getMoneyUnit(CurrencyType type);

	@Deprecated
	String getMonetaryUnit(CurrencyType type);

	String getCurrency(CurrencyType type);

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
	 * 获取一个玩家的货币数量. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 * @param type   货币类型. true: 货币2, false: 货币1
	 *
	 * @return double
	 */
	@Deprecated
	Double getMoney(String player, boolean type);

	@Deprecated
	Double getMoney(Player player, boolean type);

	Double getMoney(String player, CurrencyType type);

	Double getMoney(Player player, CurrencyType type);


	/**
	 * 获取一个玩家的货币1数量. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 *
	 * @return double
	 */
	Double getMoney(String player);

	Double getMoney(Player player);


	/**
	 * 设置一个玩家的货币数量.
	 *
	 * @param player 玩家名
	 * @param money  数量
	 * @param type   货币类型. true: 货币2, false: 货币1
	 */
	@Deprecated
	void setMoney(String player, double money, boolean type);

	@Deprecated
	void setMoney(Player player, double money, boolean type);

	void setMoney(String player, double money, CurrencyType type);

	void setMoney(Player player, double money, CurrencyType type);


	/**
	 * 设置一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param money  数量
	 */
	void setMoney(String player, double money);

	void setMoney(Player player, double money);


	/**
	 * 获取一个玩家的银行储蓄. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 *
	 * @return double
	 */
	Double getBank(Player player);

	Double getBank(String player);

	/**
	 * 设置一个玩家的银行储蓄
	 *
	 * @param player 玩家名
	 * @param money  数量
	 *
	 * @see Money#setBank(String, double)
	 * @see Money#setBank(Player, double)
	 */
	void setBank(String player, double money);

	void setBank(Player player, double money);

	/**
	 * 增加一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, double)})
	 *
	 * @see Money#addMoney(String, double)
	 * @see Money#addMoney(Player, double)
	 */
	void addMoney(String player, double amount);

	void addMoney(Player player, double amount);


	/**
	 * 增加一个玩家的货币数量
	 *
	 * @param player 玩家名
	 * @param amount 数量. 可以负数 (负数时会调用 {@link #reduceMoney(Player, double, boolean)})
	 * @param type   货币类型. true: 货币2, false: 货币1
	 *
	 * @see Money#addMoney(String, double)
	 * @see Money#addMoney(Player, double)
	 */
	@Deprecated
	void addMoney(Player player, double amount, boolean type);

	@Deprecated
	void addMoney(String player, double amount, boolean type);

	void addMoney(Player player, double amount, CurrencyType type);

	void addMoney(String player, double amount, CurrencyType type);


	/**
	 * 减少一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param amount 数量.
	 *
	 * @see Money#reduceMoney(String, double)
	 * @see Money#reduceMoney(Player, double)
	 */
	void reduceMoney(String player, double amount);

	void reduceMoney(Player player, double amount);


	/**
	 * 减少一个玩家的货币数量
	 *
	 * @param player 玩家名
	 * @param amount 数量.
	 * @param type   货币类型. true: 货币2, false: 货币1
	 *
	 * @see Money#reduceMoney(String, double)
	 * @see Money#reduceMoney(Player, double)
	 */
	@Deprecated
	void reduceMoney(Player player, double amount, boolean type);

	@Deprecated
	void reduceMoney(String player, double amount, boolean type);

	void reduceMoney(Player player, double amount, CurrencyType type);

	void reduceMoney(String player, double amount, CurrencyType type);


	/**
	 * 设置全服玩家(数据库中所有已记录的玩家)的货币数量
	 *
	 * @param amount 数量
	 *
	 * @since Money 2.0.0
	 */
	void setAllMoney(double amount);

	void setAllMoney(double amount, CurrencyType type);
}

