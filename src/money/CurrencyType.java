package money;

/**
 * 货币种类
 * 获取 String 值方法: {@link MoneyAPI#getMoneyUnit(CurrencyType)}
 * 此枚举复写了 {@link Object#toString()}, 该方法返回值将变为指定货币在 "Config.yml" 中配置的值如 "Coin"
 * <br/>
 * Currency type
 * The way to get String value: {@link MoneyAPI#getMoneyUnit(CurrencyType)}
 * The enum is overridden {@link Object#toString()}, This method will return the value in "Config.yml" like "Coin"
 *
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public enum CurrencyType {
	/**
	 * 货币 1.
	 * 可在 "Config.yml" 中修改 (项目 "money-unit-1")
	 * 默认值为 "Coin"
	 * <br/>
	 * The first currency type
	 * It can be modified in "Config.yml" (Section "money-unit-1")
	 * Default value is "Coin"
	 */
	FIRST(false),

	/**
	 * 货币 2.
	 * 可在 "Config.yml" 中修改 (项目 "money-unit-2")
	 * 默认值为 "Point"
	 * <br/>
	 * The first currency type
	 * It can be modified in "Config.yml" (Section "money-unit-2")
	 * Default value is "Point"
	 */
	SECOND(true);


	private boolean booleanValue;

	CurrencyType(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public boolean booleanValue() {
		return booleanValue;
	}

	@Override
	public String toString() {
		return MoneyAPI.getInstance().getMoneyUnit(this);
	}
}
