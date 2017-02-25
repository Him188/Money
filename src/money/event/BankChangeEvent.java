package money.event;

import money.Money;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

/**
 * 当玩家的银行数据被改动时触发本事件
 *
 * PS: 只有货币1才能存入银行
 *
 * @see Money#setBank(String, Double)
 */
public class BankChangeEvent extends Event implements Cancellable {
	protected String player;
	protected double target;
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
		return handlers;
	}
	public BankChangeEvent(String player, double target) {
		this.player = player;
		this.target = target;
	}
	public BankChangeEvent(Player player, double target) {
		this(player.getName(), target);
	}

	public String getPlayer() {
		return this.player;
	}

	public double getOriginal() {
		return Money.getInstance().getBank(this.player);
	}

	public double getTarget() {
		return this.target;
	}

	public void setTarget(double target){
		this.target = target;
	}
}
