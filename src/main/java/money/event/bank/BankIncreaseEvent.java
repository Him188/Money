package money.event.bank;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 银行余额增加事件.
 *
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class BankIncreaseEvent extends BankEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlers() {
		return handlers;
	}

	///**
	// * @see BankSaveCommand
	// */
	//public static final int CAUSE_COMMAND = 1;
	///**
	// * @see Money#addBank(String, float)
	// */
	//public static final int CAUSE_METHOD = 2;
	//public static final int CAUSE_CUSTOM = 3;
	// TODO: 2017/5/3 Event Causes


	private final String player;        //被修改经济数据的玩家
	private float amount;

	public BankIncreaseEvent(final String player, final float amount) {
		this.player = player;
		this.amount = amount;
	}

	public String getPlayer() {
		return player;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(final float amount) {
		this.amount = amount;
	}
}
