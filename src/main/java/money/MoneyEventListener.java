package money;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;

/**
 * @author Him188 @ Money Project
 * @since Money 2.0.0
 */
public class MoneyEventListener implements Listener {
	private final Money money;

	public MoneyEventListener(Money money){
		this.money = money;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		money.db.hashSet(event.getPlayer().getName(), "money1", money.getConfig().getFloat("initial-money-1", 0));
		money.db.hashSet(event.getPlayer().getName(), "money2", money.getConfig().getFloat("initial-money-2", 0));
		money.db.hashSet(event.getPlayer().getName(), "bank", money.getConfig().getFloat("initial-bank-money", 0));
	}
}
