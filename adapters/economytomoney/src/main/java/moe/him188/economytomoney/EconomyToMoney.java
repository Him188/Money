package moe.him188.economytomoney;

import cn.nukkit.Server;
import cn.nukkit.plugin.PluginBase;

/**
 * @author Him188moe @ Money Project
 */
public class EconomyToMoney extends PluginBase {
    private EventAdapterListener listener;

    @Override
    public void onEnable() {
        if (listener == null) {
            listener = new EventAdapterListener();
        }
        Server.getInstance().getPluginManager().registerEvents(listener, this);
    }
}
