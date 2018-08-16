package money;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import me.onebone.economyapi.event.money.AddMoneyEvent;
import me.onebone.economyapi.event.money.ReduceMoneyEvent;
import me.onebone.economyapi.event.money.SetMoneyEvent;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;

/**
 * Used to pass events from EconomyAPI.
 *
 * @author Him188 @ Money Project
 * @see AddMoneyEvent
 * @see ReduceMoneyEvent
 * @see SetMoneyEvent
 */
public class MoneyEventListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAddMoney(AddMoneyEvent event) {
        Server.getInstance().getPluginManager().callEvent(new MoneyIncreaseEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onReduceMoney(ReduceMoneyEvent event) {
        Server.getInstance().getPluginManager().callEvent(new MoneyDecreaseEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSetMoney(SetMoneyEvent event) {
        Server.getInstance().getPluginManager().callEvent(new MoneyChangeEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST));
    }
}