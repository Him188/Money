package money.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import money.Money;
import money.Utils;

import java.util.Map;
import java.util.Objects;

/**
 * @author Him188 @ Money Project
 * @since Money 1.0.0
 */
public class Pay1Command extends MoneyCommand {
	public Pay1Command(String name, Money owner, String[] aliases, Map<String, CommandParameter[]> commandParameters) {
		super(name, owner, aliases, commandParameters);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!this.testPermission(sender)) {
			sender.sendMessage(this.getPlugin().translateMessage("has-no-permission"));
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(this.getPlugin().translateMessage("use-in-game"));
			return true;
		}

		if (args.length < 2) {
			sender.sendMessage(this.getPlugin().translateMessage("pay-format-error", this.getName()));
			return true;
		}

		double to = Double.parseDouble(args[1]);
		double money = getPlugin().getMoney((Player) sender);
		if (money < to) {
			sender.sendMessage(this.getPlugin().translateMessage("pay-value-error"));
			return true;
		}
		if (money < 0) {
			sender.sendMessage(this.getPlugin().translateMessage("pay-value-error-1"));
			return true;
		}
		if (money - to < Double.parseDouble(getPlugin().getConfig().get("pay-1-limit").toString())) {
			sender.sendMessage(this.getPlugin().translateMessage("pay-can-not-less-than-initiation", Math.round(Double.parseDouble(getPlugin().getConfig().get("pay-1-limit").toString())), getPlugin().getMoneyUnit1()));
			return true;
		}

		Player p = Utils.getPlayer(args[0]);
		String name;
		if (p == null) {
			name = args[0];
		} else {
			p.sendMessage(this.getPlugin().translateMessage("pay-for-you", Math.round(Double.parseDouble(args[1])), getPlugin().getMonetaryUnit1()));
			name = p.getName();
		}

		if (Objects.equals(name, "")) {
			sender.sendMessage(this.getPlugin().translateMessage("invalid-name", this.getName()));
			return true;
		}


		getPlugin().reduceMoney((Player) sender, to);
		getPlugin().addMoney((Player) sender, to);

		sender.sendMessage(this.getPlugin().translateMessage("pay-success", Math.round(Double.parseDouble(args[1])), getPlugin().getMoneyUnit1(), name));
		return true;
	}
}
