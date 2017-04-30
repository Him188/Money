package money;

import cn.nukkit.scheduler.PluginTask;

import java.util.Date;

class BankInterestTask extends PluginTask<Money> {
	private int count = 0;

	public BankInterestTask(Money money) {
		super(money);
	}

	@Override
	public void onRun(int currentTick) {
		count += 1;
		if (count == 2) {
			getOwner().save();
			count = 0;
		}

		if (getOwner().bank_time > 0 && new Date().getTime() - getOwner().last_time >= getOwner().bank_time) {
			getOwner().last_time += getOwner().bank_time;
			getOwner().data.data.replaceAll((key, value) -> {
				value.put("bank", Double.toString(Double.parseDouble(value.getOrDefault("bank", "0")) * getOwner().bank_interest));
				return value;
			});
		}
	}
}
