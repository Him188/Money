package money.event.bank;

import cn.nukkit.event.Cancellable;

import java.util.Set;

/**
 * 银行利息发放事件
 *
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class BankInterestEvent extends BankEvent implements Cancellable {
	/**
	 * 利率, 值为 0 时不发放.
	 */
	private float interest;

	/**
	 * 即将利息的玩家列表
	 */
	private Set<String> players;

	public BankInterestEvent(final float interest, final Set<String> players) {
		this.interest = interest;
		this.players = players;
	}


	public Set<String> getPlayers() {
		return players;
	}

	public void setPlayers(final Set<String> players) {
		this.players = players;
	}


	public float getInterest() {
		return interest;
	}

	public void setInterest(final float interest) {
		this.interest = interest;
	}
}
