package money.event.bank;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import money.CurrencyType;
import money.Money;

/**
 * 当玩家的银行数据被改动时触发本事件
 *
 * 需要注意的是, 只有货币 1({@link CurrencyType#FIRST}) 才能存入银行
 *
 * @author Him188 @ Money Project
 */
public class BankChangeEvent extends BankEvent implements Cancellable {
	private String player;
	private float target;
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
		return handlers;
	}

	public BankChangeEvent(String player, float target) {
		this.player = player;
		this.target = target;
	}

	public BankChangeEvent(Player player, float target) {
		this(player.getName(), target);
	}

	public String getPlayer() {
		return this.player;
	}

	/**
	 * @deprecated 请直接使用 {@link Money#getBank(String)}
	 */
	@Deprecated
	public float getOriginal() {
		return Money.getInstance().getBank(this.player);
	}

	public float getTarget() {
		return this.target;
	}

	public void setTarget(float target) {
		this.target = target;
	}
}
