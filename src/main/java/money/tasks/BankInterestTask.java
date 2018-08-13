package money.tasks;

import cn.nukkit.scheduler.PluginTask;
import money.Money;
import money.event.bank.BankInterestEvent;

public class BankInterestTask extends PluginTask<Money> {
    private final long bankTime;
    private final float bankInterest;
    private final boolean real;

    private long lastTime;

    public BankInterestTask(Money money, long bankTime, float bankInterest, long lastTime, boolean real) {
        super(money);
        this.bankTime = bankTime;
        this.bankInterest = bankInterest;
        this.lastTime = lastTime;
        this.real = real;
    }

    // TODO: 2017/5/2 analyze

    @Override
    public void onRun(int currentTick) {
        if (lastTime == 0) {
            lastTime = System.currentTimeMillis();
            getOwner().updateBankLastTime(lastTime);
        }

        if (bankTime > 0 && System.currentTimeMillis() - lastTime >= bankTime) {
            BankInterestEvent event = new BankInterestEvent(bankInterest, getOwner().getPlayers());
            getOwner().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                for (String s : event.getPlayers()) {
                    getOwner().addBank(s, getOwner().getBank(s) * event.getInterest());
                }
            }

            if (real) {
                lastTime += bankTime;
                onRun(currentTick);
            } else {
                lastTime = System.currentTimeMillis();
            }

            getOwner().updateBankLastTime(lastTime);
        }
    }
}
