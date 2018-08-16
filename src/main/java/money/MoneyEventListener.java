package money;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import net.mamoe.moedb.AbstractDatabase;

/**
 * @author Him188 @ Money Project
 */
public class MoneyEventListener implements Listener {
    private final Money money;

    public MoneyEventListener(Money money) {
        this.money = money;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        AbstractDatabase child = money.db.getChildDatabase(event.getPlayer().getName());
        if (child == null || child.size() == 0) {
            money.createAccount(
                    event.getPlayer().getName(), (float) money.getConfig().getDouble("initial-money-1", 0),
                    (float) money.getConfig().getDouble("initial-money-2", 0),
                    (float) money.getConfig().getDouble("initial-bank-money", 0)
            );
        }
    }
}