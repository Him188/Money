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
 * Used to pass events from Money.
 *
 * @author Him188moe @ Money Project
 */
public class EventAdapterListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onAddMoney(MoneyIncreaseEvent event) {
        if (event.getCurrencyType() != CurrencyType.FIRST) {
            return;
        }
        AddMoneyEvent e = new AddMoneyEvent(event.getPlayer(), event.getAmount());
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setAmount((float) e.getAmount());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onReduceMoney(MoneyDecreaseEvent event) {
        if (event.getCurrencyType() != CurrencyType.FIRST) {
            return;
        }
        ReduceMoneyEvent e = new ReduceMoneyEvent(event.getPlayer(), event.getAmount());
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setAmount((float) e.getAmount());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onSetMoney(MoneyChangeEvent event) {
        if (event.getCurrencyType() != CurrencyType.FIRST) {
            return;
        }
        SetMoneyEvent e = new SetMoneyEvent(event.getPlayer(), event.getTarget());
        Server.getInstance().getPluginManager().callEvent(e);
        if (e.isCancelled()) {
            event.setCancelled();
        }
        event.setTarget((float) e.getAmount());
    }
}
