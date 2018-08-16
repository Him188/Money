package moe.him188.moneytoeconomy;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;

/**
 * @author Him188moe @ Money Project
 */
public class MoneyToEconomy extends PluginBase {
    private EventAdapterListener listener;

    @Override
    public void onEnable() {
        if (listener == null) {
            listener = new EventAdapterListener();
        }
        Server.getInstance().getPluginManager().registerEvents(listener, this);
    }
}
