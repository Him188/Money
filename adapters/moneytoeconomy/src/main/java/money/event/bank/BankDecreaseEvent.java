package money.event.bank;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;

/**
 * 银行余额减少事件
 *
 * @author Him188 @ Money Project
 */
public class BankDecreaseEvent extends BankEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    private final String player;        //被修改经济数据的玩家
    private float amount;

    public BankDecreaseEvent(final String player, final float amount) {
        this.player = player;
        this.amount = amount;
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
