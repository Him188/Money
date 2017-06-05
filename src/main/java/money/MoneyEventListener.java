package money;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;

/**
 * @author Him188 @ Money Project
 */
public class MoneyEventListener implements Listener {
	private final Money money;

	public MoneyEventListener(Money money) {
		this.money = money;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		if (!money.db.hashExits(event.getPlayer().getName(), "money1")) {
			money.setMoney(event.getPlayer(), (float) money.getConfig().getDouble("initial-money-1", 0), CurrencyType.FIRST);
			money.setMoney(event.getPlayer(), (float) money.getConfig().getDouble("initial-money-2", 0), CurrencyType.SECOND);
			money.setBank(event.getPlayer(), (float) money.getConfig().getDouble("initial-bank-money", 0));
		}

	}
}