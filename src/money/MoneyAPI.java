package money;

import cn.nukkit.Player;

/**
 * Money API
 *
 * @see Money
 */
public interface MoneyAPI {
	/**
	 * @return 插件已开启时 Money; 未开启时 null
	 * @see Money#getInstance()
	 */
	static Money getInstance() {
		return Money.getInstance();
	}

	/**
	 * 获取第一种货币
	 *
	 * @return String
	 * @see Money#getMoneyUnit1()
	 * @see Money#getMonetaryUnit1()
	 */
	String getMoneyUnit1();

	String getMonetaryUnit1();

	String getMoneyUnit(boolean unit);

	/**
	 * 获取第二种货币.
	 *
	 * @return String
	 * @see Money#getMoneyUnit2()
	 * @see Money#getMonetaryUnit2()
	 */
	String getMoneyUnit2();

	String getMonetaryUnit2();

	String getMonetaryUnit(boolean unit);

	/**
	 * 检查第二种货币是否已开启
	 *
	 * @return boolean
	 * @see Money
	 */
	boolean isMoneyUnit2Enabled();


	/**
	 * 获取一个玩家的货币数量. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 * @param type   货币类型. true: 货币2, false: 货币1
	 * @return double
	 * @see Money#getMoney(String, boolean)
	 * @see Money#getMoney(Player, boolean)
	 */
	Double getMoney(String player, boolean type);


	Double getMoney(Player player, boolean type);


	/**
	 * 获取一个玩家的货币1数量. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 * @return double
	 * @see Money#getMoney(String)
	 * @see Money#getMoney(Player)
	 */
	Double getMoney(String player);


	Double getMoney(Player player);


	/**
	 * 设置一个玩家的货币数量.
	 *
	 * @param player 玩家名
	 * @param money  数量
	 * @param type   货币类型. true: 货币2, false: 货币1
	 * @see Money#setMoney(String, double, boolean)
	 * @see Money#setMoney(Player, double, boolean)
	 */
	void setMoney(String player, double money, boolean type);

	void setMoney(Player player, double money, boolean type);


	/**
	 * 设置一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param money  数量
	 * @see Money#setMoney(String, double)
	 * @see Money#setMoney(Player, double)
	 */
	void setMoney(String player, double money);

	void setMoney(Player player, double money);


	/**
	 * 获取一个玩家的银行储蓄. 如果玩家不存在, 将返回 null
	 *
	 * @param player 玩家名
	 * @return double
	 * @see Money#getBank(String)
	 * @see Money#getBank(Player)
	 */

	Double getBank(Player player);


	Double getBank(String player);

	/**
	 * 设置一个玩家的银行储蓄
	 *
	 * @param player 玩家名
	 * @param money  数量
	 * @see Money#setBank(String, double)
	 * @see Money#setBank(Player, double)
	 */
	void setBank(String player, double money);

	void setBank(Player player, double money);

	/**
	 * 增加一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param amount 数量
	 * @see Money#addMoney(String, double)
	 * @see Money#addMoney(Player, double)
	 */
	void addMoney(String player, double amount);

	void addMoney(Player player, double amount);


	/**
	 * 增加一个玩家的货币1数量
	 *
	 * @param player 玩家名
	 * @param amount 数量
	 * @param type   货币类型. true: 货币2, false: 货币1
	 * @see Money#addMoney(String, double)
	 * @see Money#addMoney(Player, double)
	 */
	void addMoney(Player player, double amount, boolean type);

	void addMoney(String player, double amount, boolean type);
}
