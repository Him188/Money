package money.event;

import money.Money;
import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

/**
 * 当玩家的经济数据被改动时触发本事件
 *
 * @see Money#setMoney(String, Double, Boolean)
 */
public class MoneyChangeEvent extends Event implements Cancellable {
	protected String player = null;
	protected double target;
	protected boolean type = false;
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
		return handlers;
	}
	public MoneyChangeEvent(String player, double target, boolean type) {
		this.player = player;
		this.target = target;
		this.type = type;
	}

	public MoneyChangeEvent(Player player, double target, boolean type){
		this(player.getName(), target, type);
	}

	public String getPlayer() {
		return this.player;
	}

	public double getOriginal() {
		return Money.getInstance().getMoney(this.player, this.type);
	}

	public double getTarget() {
		return this.target;
	}

	/**
	 * 获取货币类型
	 *
	 * @return false: 货币 1, true: 货币 2
	 */
	public boolean getMoneyType(){
		return this.type;
	}

	public void setTarget(double target){
		this.target = target;
	}
}
