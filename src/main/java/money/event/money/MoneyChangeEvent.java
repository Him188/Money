package money.event.money;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import money.CurrencyType;
import money.Money;

/**
 * 当玩家的货币数量被改动时触发本事件
 *
 * @author Him188 @ Money Project
 * @since Money 1.0.0
 */
public class MoneyChangeEvent extends MoneyEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
		return handlers;
	}


	private final String player;        //被修改经济数据的玩家
	private float target;              //最终的货币数量
	private final CurrencyType type;    //货币种类

	@Deprecated
	public MoneyChangeEvent(String player, float target, boolean type) {
		this(player, target, CurrencyType.fromBoolean(type));
	}

	@Deprecated
	public MoneyChangeEvent(Player player, float target, boolean type) {
		this(player.getName(), target, CurrencyType.fromBoolean(type));
	}

	public MoneyChangeEvent(String player, float target, CurrencyType type) {
		this.player = player;
		this.target = target;
		this.type = type;
	}

	public MoneyChangeEvent(Player player, float target, CurrencyType type) {
		this(player.getName(), target, type);
	}

	public String getPlayer() {
		return this.player;
	}

	/**
	 * @deprecated 请直接使用 {@link Money#getMoney(String, CurrencyType)}
	 */
	@Deprecated
	public float getOriginal() {
		return Money.getInstance().getMoney(this.player, this.type);
	}

	public float getTarget() {
		return this.target;
	}

	/**
	 * 获取货币类型
	 *
	 * @return false: 货币 1, true: 货币 2
	 *
	 * @deprecated 请使用 {@link #getCurrencyType()}, 本方法仅为兼容旧 API 而保留, 将来版本可能删除!
	 */
	@Deprecated
	public boolean getMoneyType() {
		return this.type.booleanValue();
	}

	/**
	 * 获取货币类型
	 *
	 * @return 货币类型
	 */
	public CurrencyType getCurrencyType() {
		return this.type;
	}

	public void setTarget(float target) {
		this.target = target;
	}
}
