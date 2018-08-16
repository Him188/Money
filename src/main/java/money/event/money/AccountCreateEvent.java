package money.event.money;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import money.MoneyAPI;

/**
 * 当创建账户时触发事件
 * <p>
 * {@link MoneyAPI#createAccount(String, float, float, float)}
 *
 * @author Him188moe @ Money Project
 * @since 3.2
 */
public class AccountCreateEvent extends MoneyEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }


    private final String player;
    private float money1;
    private float money2;
    private float bank;

    public AccountCreateEvent(String player, float money1, float money2, float bank) {
        this.player = player;
        this.money1 = money1;
        this.money2 = money2;
        this.bank = bank;
    }

    public String getPlayer() {
        return this.player;
    }

    public float getMoney1() {
        return money1;
    }

    public float getMoney2() {
        return money2;
    }

    public float getBank() {
        return bank;
    }

    public void setMoney1(float money1) {
        this.money1 = money1;
    }

    public void setMoney2(float money2) {
        this.money2 = money2;
    }

    public void setBank(float bank) {
        this.bank = bank;
    }
}
