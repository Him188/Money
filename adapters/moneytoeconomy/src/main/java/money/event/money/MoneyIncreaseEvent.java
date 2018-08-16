package money.event.money;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import money.CurrencyType;

/**
 * 货币数量 (余额)增加事件
 *
 * @author Him188 @ Money Project
 */
public class MoneyIncreaseEvent extends MoneyEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    private final String player;        //被修改经济数据的玩家
    private float amount;
    private final CurrencyType type;

    public MoneyIncreaseEvent(final String player, final float amount, final CurrencyType type) {
        this.player = player;
        this.amount = amount;
        this.type = type;
    }

    public CurrencyType getCurrencyType() {
        return type;
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
