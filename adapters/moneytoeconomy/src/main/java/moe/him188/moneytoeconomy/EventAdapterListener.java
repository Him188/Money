package moe.him188.moneytoeconomy;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import me.onebone.economyapi.event.money.AddMoneyEvent;
import me.onebone.economyapi.event.money.ReduceMoneyEvent;
import me.onebone.economyapi.event.money.SetMoneyEvent;
import money.CurrencyType;
import money.event.money.MoneyChangeEvent;
import money.event.money.MoneyDecreaseEvent;
import money.event.money.MoneyIncreaseEvent;

/**
 * Used to pass events from EconomyAPI.
 *
 * @author Him188moe @ Money Project
 * @see AddMoneyEvent
 * @see ReduceMoneyEvent
 * @see SetMoneyEvent
 */
public class EventAdapterListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAddMoney(AddMoneyEvent event) {
        MoneyIncreaseEvent e = new MoneyIncreaseEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST);
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setAmount(e.getAmount());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onReduceMoney(ReduceMoneyEvent event) {
        MoneyDecreaseEvent e = new MoneyDecreaseEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST);
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setAmount(e.getAmount());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSetMoney(SetMoneyEvent event) {
        MoneyChangeEvent e = new MoneyChangeEvent(event.getPlayer(), (float) event.getAmount(), CurrencyType.FIRST);
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setAmount(e.getTarget());
    }
}