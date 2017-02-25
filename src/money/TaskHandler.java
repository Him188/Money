package money;

import cn.nukkit.scheduler.Task;

import java.util.Date;

class TaskHandler extends Task {
	private int count = 0;
	private final Money plugin;

	public TaskHandler(Money money) {
		plugin = money;
	}

	@Override
	public void onRun(int currentTick) {
		count += 1;
		if (count == 2) {
			plugin.save();
			count = 0;
		}

		if (plugin.bank_time > 0 && new Date().getTime() - plugin.last_time >= plugin.bank_time) {
			plugin.last_time += plugin.bank_time;
			Database.data.replaceAll((key, value) -> {
				value.put("bank", Double.toString(Double.parseDouble(value.getOrDefault("bank", "0")) * plugin.bank_interest));
				return value;
			});
		}
	}
}
